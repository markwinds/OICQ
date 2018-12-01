package org.fitzeng.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fitzeng.db.DBManager;
import org.fitzeng.main.ChatManager;
import org.fitzeng.view.MainWindow;

public class ChatSocket extends Thread{

	private String username;
	private Socket socket;
	private String message = null;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
    private Connection connection = DBManager.getDBManager().getConnection();
    private Statement statement;
    private ResultSet resultSet;
    private String sql;
    private SocketMsg socketMsg;
	
	public ChatSocket(Socket s) {
		this.socket = s;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));		//以UTF-8格式读取输入/输出流
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void run() {
		try {
			try {
				if(connection==null)	//数据库在过了一段时间没有连接请求后就会断开，所以这里在新建线程的时候检测是否断开，断开的话就重连
				{
					DBManager.getDBManager().connectDB();	//重连
					connection = DBManager.getDBManager().getConnection();
				}
				statement = connection.createStatement();
			}catch(SQLException e) {
				e.printStackTrace();
			}
			String line = null;
			System.out.println("thread is builded");
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.equals("-1")) {	
					if(message==null){
						message=line;
					}else{
						message += line;
					}
				} else {
					System.out.println("");
					System.out.println("receive : " + message);		//控制台显示接受到的数据
					delMessage(message);							//处理接收到的数据
					line = null;
					message = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("thread is over!!!");
			try {
				try {
					String sql = "UPDATE UserInfo SET signed = '0' WHERE username = '" + username + "';";	//标记用户下线
					Statement statement = connection.createStatement();
					statement.execute(sql);
				}catch (SQLException e) {
					e.printStackTrace();
				}
				MainWindow.getMainWindow().setShowMsg(this.username + " login out !");
				MainWindow.getMainWindow().removeOfflineUsers(this.username);
				ChatManager.getChatManager().remove(socketMsg);
				bufferedWriter.close();
				bufferedReader.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	//处理数据,根据数据头处理不同的数据
	//------------------------------------------改成自己通讯协议用的数据头----------------------------------------
	public void delMessage(String msg) {
		if (msg != null) {
			String action = getAction(msg);
			switch(action) {
				case "LOGIN": { dealLogin(msg); break; }
				case "REGISTER": { dealRegister(msg); break; }
				case "ADDFRIEND": { dealAddFriend(msg); break; }
				case "FEEDBACKFRIEND": { dealFeedbackFriend(msg); break; }
				case "SENDMESSAGE": { dealSendMessage(msg); break; }
				default : break;
			}
		}
	}
	

	//发送数据
    public void sendMsg(String msg) {
    	try {
    		while (socket == null) ;
            if (bufferedWriter != null) {
            	System.out.println("send :" + msg);		//在控制台显示要发送的数据
                bufferedWriter.write(msg + "\n");
                bufferedWriter.flush();
                bufferedWriter.write("-1\n");			//在每个数据后加-1结尾
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getMsg() {
    	return message;
    }


    private void dealRegister(String msg) {
    	try {
    		String iusername = null;		//用来存放解析出来的用户名
        	String iPassword = null;		//用来存放解析出来的密码
        	String p = "\\[REGISTER\\]:\\[(.*), (.*)\\]";		//括号前加\\转义，其他的照常写，（）内就是提取出来的一组一组的数据
        	Pattern pattern = Pattern.compile(p);
        	Matcher matcher = pattern.matcher(msg);
        	if (matcher.find()) {			//group配对之前一定先调用find函数
        		iusername = matcher.group(1);
        		iPassword = matcher.group(2);
    		}
    		sql="SELECT * FROM UserInfo WHERE username = '" + iusername + "';";
			resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				sendMsg("[ACKREGISTER]:[2]");		//返回2代表用户名已经存在
				return;
			}
    		sql="INSERT INTO UserInfo VALUES ('" + iusername + "','" + iPassword + "','0','0');";
			statement.execute(sql);	//执行数据库语句
			this.username = iusername;
			sendMsg("[ACKREGISTER]:[1]");
			return ;		//返回成功反馈后就结束，不会执行下面的sendMsg("[ACKLOGIN]:[0]")
        	//sendMsg("[ACKREGISTER]:[0]");
    	}catch (SQLException e) {
			e.printStackTrace();
		}
    }

    private void dealLogin(String msg) {
    	try {
    		String iusername = null;
        	String iPassword = null;
        	String p = "\\[LOGIN\\]:\\[(.*), (.*)\\]";		//括号前加\\转义，其他的照常写，（）内就是提取出来的一组一组的数据
        	Pattern pattern = Pattern.compile(p);
        	Matcher matcher = pattern.matcher(msg);
        	if (matcher.find()) {
        		iusername = matcher.group(1);
        		iPassword = matcher.group(2);
        	}
    		sql = "SELECT password FROM UserInfo WHERE username = '" + iusername + "';";	//SELECT password FROM USERS WHERE username = ' + iusername + '; 字符串拼接	
			resultSet = statement.executeQuery(sql);	//执行数据库语句
			if (resultSet.next() && iPassword.equals(resultSet.getString(1)) ) {	//resultSet.getString(1)中的1代表的是第1列，列数编号从1开始，参数也可以是列的名字
				sendMsg("[ACKLOGIN]:[1]");		//向客户端发送成功信息
				this.username = iusername;
				MainWindow.getMainWindow().setShowMsg(this.username + " login in!");
				MainWindow.getMainWindow().addOnlineUsers(this.username);
				socketMsg = new SocketMsg(this,  this.username);		//某用户拥有的某线程
				ChatManager.getChatManager().add(socketMsg);
				try {
					Thread.sleep(1000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				updataNewLogin();
				updataUserInfo(iusername,"signed","1");
				return ;		//返回成功反馈后就结束，不会执行下面的sendMsg("[ACKLOGIN]:[0]")
			}else {
				sendMsg("[ACKLOGIN]:[0]");
			}
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    }

	public void dealAddFriend(String msg){
		try {
			String origin = null;
	    	String aim = null;
			String remark=null;
			ChatManager chatManager=ChatManager.getChatManager();
	    	String p = "\\[ADDFRIEND\\]:\\[(.*), (.*), (.*)\\]";		
	    	Pattern pattern = Pattern.compile(p);
	    	Matcher matcher = pattern.matcher(msg);
	    	if (matcher.find()) {
	    		origin = matcher.group(1);
	    		aim = matcher.group(2);
				remark = matcher.group(3);
	    	}
			if(origin.equals(aim)){
				sendMsg("[ACKADDFRIEND]:[2]");		//不能加自己为好友
				return;
			}
			sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	
			resultSet = statement.executeQuery(sql);	
			if(!resultSet.next()){									//如果没有找到用户
				sendMsg("[ACKADDFRIEND]:[3]");						//数据库中没有这个用户
				return;
			}
			sendMsg("[ACKADDFRIEND]:[1]");
			sql="SELECT * FROM Apply WHERE origin = '" + origin + "' && aim = '"+aim+"';";	//检测申请表中是否已经有这一条记录
			resultSet = statement.executeQuery(sql);
			if(!resultSet.next()){
				sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	
				resultSet = statement.executeQuery(sql);
				resultSet.next();
				if (resultSet.getString("signed").equals("1")) {		//如果该用户在线
				ChatSocket chatSocket = chatManager.getSocketMsg(aim).getChatSocket();
				String msg1 = "[SERAPPLYFRIEND]:[" + origin + ", "+ aim + ", " + remark + "]";
				chatSocket.sendMsg(msg1);	
				}else{	//用户不在线就将信息写入申请表
					sql="INSERT INTO Apply VALUES ('" + origin + "','" + aim + "','"+remark+"');";
					doExecute(sql);
					updataUserInfo(aim,"updates","1");
				}
			}
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    

	public void dealFeedbackFriend(String msg){
		try {
			String origin = null;
	    	String aim = null;
			String status=null;
			ChatManager chatManager=ChatManager.getChatManager();
	    	String p = "\\[FEEDBACKFRIEND\\]:\\[(.*), (.*), (.*)\\]";		
	    	Pattern pattern = Pattern.compile(p);
	    	Matcher matcher = pattern.matcher(msg);
	    	if (matcher.find()) {
	    		origin = matcher.group(1);
	    		aim = matcher.group(2);
				status = matcher.group(3);
	    	}
	    	if(status.equals("1")) {
    			sql="INSERT INTO Friends VALUES(\""+origin+"\", \""+aim+"\");";
    			statement.execute(sql);
    			sql="INSERT INTO Friends VALUES(\""+aim+"\", \""+origin+"\");";
    			statement.execute(sql);
	    	}
			sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	
			resultSet = statement.executeQuery(sql);	
			resultSet.next();
			if (resultSet.getString("signed").equals("1")) {		//如果该用户在线
				ChatSocket chatSocket = chatManager.getSocketMsg(aim).getChatSocket();
				String msg1 = "[SERRESPONDFRIEND]:[" + origin + ", "+ aim + ", " + status + "]";
				chatSocket.sendMsg(msg1);	
			}else{			
				sql="INSERT INTO Feedback VALUES ('" + origin + "','" + aim + "','"+status+"');";
				doExecute(sql);
				updataUserInfo(aim,"updates","1");
			}
			sendMsg("[ACKFEEDBACKFRIEND]:[1]");
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void dealSendMessage(String msg) {
		try {
			String origin = null;
	    	String aim = null;
			String content=null;
			ChatManager chatManager=ChatManager.getChatManager();
	    	String p = "\\[SENDMESSAGE\\]:\\[(.*), (.*), (.*)\\]";		
	    	Pattern pattern = Pattern.compile(p);
	    	Matcher matcher = pattern.matcher(msg);
	    	if (matcher.find()) {
	    		origin = matcher.group(1);
	    		aim = matcher.group(2);
	    		content = matcher.group(3);
	    	}
			sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	
			resultSet = statement.executeQuery(sql);	
			resultSet.next();
			if (resultSet.getString("signed").equals("1")) {		//如果该用户在线
				ChatSocket chatSocket = chatManager.getSocketMsg(aim).getChatSocket();
				String msg1 = "[SERMESSAGE]:[" + origin + ", "+ aim + ", " + content + "]";
				chatSocket.sendMsg(msg1);	
			}
			statement.execute("INSERT INTO Message VALUES(\""+origin+"\", \""+aim+"\", \""+content+"\");");
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	


	//----------------执行数据库语句------------------------
	public ResultSet doExecuteQuery(String sql){
		try{
			resultSet = statement.executeQuery(sql);
			return resultSet;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean doExecute(String sql){
		try{
			boolean flag = statement.execute(sql);
			return flag;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	//改变某用户的某个属性值
	public boolean updataUserInfo(String user, String attribute, String value){
		sql = "UPDATE UserInfo SET "+attribute+" = '"+value+"' WHERE username = '" + user + "';";
		//System.out.println(sql);	
		return doExecute(sql);
	}

	public void updataNewLogin(){
		try {
			sql = "SELECT * FROM UserInfo WHERE username = '" + username + "';";	
			resultSet = statement.executeQuery(sql);	
			resultSet.next();
			if (resultSet.getString("updates").equals("1")) {		//如果有数据要更新
				sql = "SELECT * FROM Apply WHERE aim = '" + username + "';";	
				resultSet = statement.executeQuery(sql);
				while(resultSet.next()){
					String origin,aim,remark;
					origin=resultSet.getString("origin");
					aim=resultSet.getString("aim");
					remark=resultSet.getString("remark");
					try {
						Thread.sleep(50);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					String msg1 = "[SERAPPLYFRIEND]:[" + origin + ", "+ aim + ", " + remark + "]";
					sendMsg(msg1);
				}
				sql = "DELETE FROM Apply WHERE aim = '" + username + "';";	
				statement.execute(sql);

				sql = "SELECT * FROM Feedback WHERE aim = '" + username + "';";	
				resultSet = statement.executeQuery(sql);
				while(resultSet.next()){
					String origin,aim,status;
					origin=resultSet.getString("origin");
					aim=resultSet.getString("aim");
					status=resultSet.getString("status");
					try {
						Thread.sleep(50);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					String msg1 = "[SERRESPONDFRIEND]:[" + origin + ", "+ aim + ", " + status + "]";
					sendMsg(msg1);
				}
				sql = "DELETE FROM Feedback WHERE aim = '" + username + "';";	
				statement.execute(sql);
			}
			updataUserInfo(username,"updates","0");
			/*-------------------下面更新朋友列表和信息列表----------------------------------------*/
			sql = "SELECT * FROM Friends WHERE username = '" + username + "';";	
			resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
				String host,frined;
				host=resultSet.getString("username");
				frined=resultSet.getString("friendsName");
				try {
					Thread.sleep(50);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				String msg1 = "[SERUPDATAFRIEND]:[" + host + ", "+ frined + "]";
				sendMsg(msg1);
			}
			sql = "SELECT * FROM Message WHERE origin = '" + username + "' || aim = '"+username+"';";	
			resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
				String origin,aim,message;
				origin=resultSet.getString("origin");
				aim=resultSet.getString("aim");
				message=resultSet.getString("message");
				try {
					Thread.sleep(50);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				String msg1 = "[SERUPDATAMSG]:[" + origin + ", "+ aim + ", " + message + "]";
				sendMsg(msg1);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}


	//获取信息头
    public String getAction(String msg) {	
        String p = "\\[(.*)\\]:";					//p为正则表达式
        Pattern pattern = Pattern.compile(p);		
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(1);				//返回msg中第一个符合正则表达式p的字符字串
        } else {
            return "error";
        }
	}
}
