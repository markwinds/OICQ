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
			String line = null;
			System.out.println("thread is builded");
			while ((line = bufferedReader.readLine()) != null) {
//				System.out.println("I am here");
				if (!line.equals("-1")) {	
					message += line;
//					System.out.println("re....");
				} else {
					System.out.println("begin to deal msg");
					delMessage(message);							//处理接收到的数据
	            	System.out.println("receive : " + message);		//控制台显示接受到的数据
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
					String sql = "UPDATE UserInfo SET signed = '0' WHERE username = '" + username + "';";
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

				case "CHATMSG": { dealChatMsg(msg); break; }
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
    

    private void dealChatMsg(String msg) {
    	String chatObj = null;
    	String content = null;
    	String avatarID = null;
    	String msgType = null;
    	String p = "\\[CHATMSG\\]:\\[(.*), (.*), (.*), (.*)\\]";
    	Pattern pattern = Pattern.compile(p);
    	Matcher matcher = pattern.matcher(msg);
    	if (matcher.find()) {
    		chatObj = matcher.group(1);
    		content = matcher.group(2);
    		avatarID = matcher.group(3);
    		msgType = matcher.group(4);
    	} else {
    		return;
    	}
    	String out = null;
    	String sqlGroup = "SELECT * FROM GROUPS WHERE groupName = '" + chatObj+ "';";
    	try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlGroup);
			// gruop chat
			if (resultSet.next()) {
				// find all group members to send msg
				String sql = "SELECT groupMemberName FROM GROUPINFO WHERE groupName = '" + chatObj + "';";
				resultSet = statement.executeQuery(sql);
				while (resultSet.next()) {					
					// if user is online , then send.
					for (SocketMsg SocketMsg : ChatManager.getChatManager().socketList) {
						if (SocketMsg.getUsername().equals(resultSet.getString(1)) && !SocketMsg.getUsername().equals(username)) {
							out = "[GETCHATMSG]:[" + username + ", " + content + ", " + avatarID + ", Text, " + chatObj + "]";
							SocketMsg.getChatSocket().sendMsg(out);
						}
					}
				}
			// private chat
			} else {
				for (SocketMsg socketManager : ChatManager.getChatManager().socketList) {
					if (socketManager.getUsername().equals(chatObj)) {
						out = "[GETCHATMSG]:[" + username + ", " + content + ", " + avatarID + ", Text,  ]";
						socketManager.getChatSocket().sendMsg(out);
					}
				}
			}
			out = "[ACKCHATMSG]:[1]";
			sendMsg(out);
		} catch (SQLException e) {
			out = "[ACKCHATMSG]:[0]";
			sendMsg(out);
			e.printStackTrace();
		}
    }



    private void dealRegister(String msg) {
		String iusername = null;
    	String iPassword = null;
    	
    	String p = "\\[REGISTER\\]:\\[(.*), (.*)\\]";		//括号前加\\转义，其他的照常写，（）内就是提取出来的一组一组的数据
    	Pattern pattern = Pattern.compile(p);
    	Matcher matcher = pattern.matcher(msg);
    	if (matcher.find()) {
    		iusername = matcher.group(1);
    		iPassword = matcher.group(2);
		}
		String sql="SELECT * FROM UserInfo WHERE username = '" + iusername + "';";
		try{
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			if(resultSet.next()){
				sendMsg("[ACKREGISTER]:[2]");				//返回2代表用户名已经存在
				return;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		sql="INSERT INTO UserInfo VALUES ('" + iusername + "','" + iPassword + "','0','0');";
		try {
			Statement statement = connection.createStatement();
			statement.execute(sql);	//执行数据库语句
			this.username = iusername;
			MainWindow.getMainWindow().setShowMsg(this.username + " sign in!");
			MainWindow.getMainWindow().addOnlineUsers(this.username);
			sendMsg("[ACKREGISTER]:[1]");
			return ;		//返回成功反馈后就结束，不会执行下面的sendMsg("[ACKLOGIN]:[0]")
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	sendMsg("[ACKREGISTER]:[0]");
    }

    private void dealLogin(String msg) {
    	String iusername = null;
    	String iPassword = null;
    	
    	String p = "\\[LOGIN\\]:\\[(.*), (.*)\\]";		//括号前加\\转义，其他的照常写，（）内就是提取出来的一组一组的数据
    	Pattern pattern = Pattern.compile(p);
    	Matcher matcher = pattern.matcher(msg);
    	if (matcher.find()) {
    		iusername = matcher.group(1);
    		iPassword = matcher.group(2);
    	}
		String sql = "SELECT password FROM UserInfo WHERE username = '" + iusername + "';";	//SELECT password FROM USERS WHERE username = ' + iusername + '; 字符串拼接
		String sql1 = "UPDATE UserInfo SET signed = '1' WHERE username = '" + iusername + "';";
		try {
			Statement statement = connection.createStatement();
			statement.execute(sql1);	
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);	//执行数据库语句
			if (resultSet.next() && iPassword.equals(resultSet.getString(1)) ) {	//resultSet.getString(1)中的1代表的是第1列，列数编号从1开始，参数也可以是列的名字
				sendMsg("[ACKLOGIN]:[1]");
				this.username = iusername;
				MainWindow.getMainWindow().setShowMsg(this.username + " login in!");
				MainWindow.getMainWindow().addOnlineUsers(this.username);
				socketMsg = new SocketMsg(this,  this.username);		//某用户拥有的某线程
				ChatManager.getChatManager().add(socketMsg);
				return ;		//返回成功反馈后就结束，不会执行下面的sendMsg("[ACKLOGIN]:[0]")
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	sendMsg("[ACKLOGIN]:[0]");
    }

	public void dealAddFriend(String msg){
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
		String sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);	
			if(!resultSet.next()){									//如果没有找到用户
				sendMsg("[ACKADDFRIEND]:[3]");						//数据库中没有这个用户
				return;
			}
			sendMsg("[ACKADDFRIEND]:[1]");
			if (resultSet.getString("signed").equals("1")) {		//如果该用户在线
				ChatSocket chatSocket = chatManager.getSocketMsg(aim).getChatSocket();
				String msg1 = "[SERAPPLYFRIEND]:[" + origin + ", "+ aim + ", " + remark + "]";
				chatSocket.sendMsg(msg1);	
			}else{	//用户不在线就将信息写入申请表
//				sql="INSERT INTO Apply VALUES ('" + origin + "','" + aim + "');";
//				statement = connection.createStatement();
//				statement.execute(sql);	
//				sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	

			}
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	sendMsg("[ACKADDFRIEND]:[0]");
	}
    

	public void dealFeedbackFriend(String msg){
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
		String sql = "SELECT * FROM UserInfo WHERE username = '" + aim + "';";	
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);	
			resultSet.next();
			if (resultSet.getString("signed").equals("1")) {		//如果该用户在线
				ChatSocket chatSocket = chatManager.getSocketMsg(aim).getChatSocket();
				String msg1 = "[SERRESPONDFRIEND]:[" + origin + ", "+ aim + ", " + status + "]";
				chatSocket.sendMsg(msg1);	
			}else{			
				
			}
			sendMsg("[ACKFEEDBACKFRIEND]:[1]");
			return;
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	sendMsg("[ACKFEEDBACKFRIEND]:[0]");
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
