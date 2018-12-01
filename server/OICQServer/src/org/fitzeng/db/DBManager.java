package org.fitzeng.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.fitzeng.view.MainWindow;

//连接数据库管理类
public class DBManager {

	private static final DBManager dbManager = new DBManager();
	private static Connection connection = null;
	
	//-------------------------------------------url要改成自己对应的数据库---------------------------------------------------------------
	
	private static final String url = "jdbc:mysql://localhost/oicq";
	private Statement statement;
	
	public static DBManager getDBManager() {
		return dbManager;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	private DBManager() { }

	public void addDBDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			MainWindow.getMainWindow().setShowMsg("load mysql driver success");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			MainWindow.getMainWindow().setShowMsg("load mysql driver failed");			
			e.printStackTrace();
		}
	}
	
	
	public void connectDB() {
		//-----------------------------------------------------用户名和密码用自己的-------------------------------------------------
		try {
			connection = DriverManager.getConnection(url, "root", "");
			MainWindow.getMainWindow().setShowMsg("connect mysql success");
		} catch (SQLException e) {
			MainWindow.getMainWindow().setShowMsg("connect mysql failed");
			e.printStackTrace();
		}
	}
	
	/**
	 * init database
	 * @throws Exception 
	 */
	//------------------------------------------根据自己的要求对数据库进行初始化操作，主要是建表---------------------------
	public void initDB() throws Exception {
		statement = connection.createStatement();

		//createTabFriends();
		//createTabApply();
		//createTabMessage();
		//createTabUserInfo();
		//createTabFeedback();

		statement.close();
	}
	
	

	private void createTabFriends() throws Exception {
		statement.execute("DROP TABLE IF EXISTS Friends;");
		statement.execute("CREATE TABLE Friends ("
				+ "username VARCHAR(10),"
				+ "friendsName VARCHAR(10));");
		//statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Stark\");");
	}
	

	private void createTabUserInfo() throws Exception {
		statement.execute("DROP TABLE IF EXISTS UserInfo;");
		statement.execute("CREATE TABLE UserInfo ("
				+ "username VARCHAR(10),"
				+ "password VARCHAR(20),"			//密码
				+ "signed VARCHAR(10),"				//是否登录,1是登录
				+ "updates VARCHAR(10),"				//是否需要刷新数据
				+ "PRIMARY KEY(username));");
		statement.execute("INSERT INTO UserInfo VALUES(\"markwinds\", \"ischen\", \"0\", \"0\");");
	}
	
	

	private void createTabApply() throws Exception{
		statement.execute("DROP TABLE IF EXISTS Apply;");	//好友申请表
		statement.execute("CREATE TABLE Apply ("			
				+ "origin VARCHAR(10),"			//申请发起人
				+ "aim    VARCHAR(10),"			//接收申请人
				+ "remark VARCHAR(300));");
	}


	private void createTabMessage() throws Exception{
	statement.execute("DROP TABLE IF EXISTS Message;");	//消息列表
	statement.execute("CREATE TABLE Message ("			
			+ "origin  VARCHAR(10),"			//申请发起人
			+ "aim     VARCHAR(10),"
			+ "message VARCHAR(1000));");		
	}

	private void createTabFeedback() throws Exception{
		statement.execute("DROP TABLE IF EXISTS Feedback;");
		statement.execute("CREATE TABLE Feedback ("			
			+ "origin  VARCHAR(10),"			//申请发起人
			+ "aim     VARCHAR(10),"
			+ "status  VARCHAR(10));");
		statement.execute("INSERT INTO Feedback VALUES(\"markwinds\", \"ischen\", \"0\");");
	}


}
