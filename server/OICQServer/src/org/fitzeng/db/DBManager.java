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
		
		// Create table Users
		//createTabUsers();
		
		// // Create table UserInfo
		
		
		// // Create table Friends
		
		
		// // Create table Groups
		// createTabGroups();
		
		// // Create table GroupInfo
		// createTabGroupInfo();


//		createTabFriends();
//		createTabApply();
//		createTabMessage();
//		createTabUserInfo();
//		createTabFeedback();
//		createTabMessage();

		statement.close();
	}
	

	private void createTabGroupInfo() throws Exception {
		statement.execute("DROP TABLE IF EXISTS GroupInfo;");
		statement.execute("CREATE TABLE GroupInfo ("
				+ "groupName VARCHAR(10),"
				+ "groupMemberName VARCHAR(10));");
		
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG1\", \"Tony\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG1\", \"Elon\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG1\", \"Musk1\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG1\", \"Musk3\");");
		
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG2\", \"Elon\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG2\", \"Musk2\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG2\", \"Musk3\");");
		
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG3\", \"Tony\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG3\", \"Stark\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"TonyG3\", \"Musk\");");
		
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG1\", \"Elon\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG1\", \"Tony\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG1\", \"Musk2\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG1\", \"Musk1\");");
		
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG2\", \"Elon\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG2\", \"Stark\");");
		statement.execute("INSERT INTO GroupInfo VALUES(\"ElonG2\", \"Musk2\");");
		
	}
	

	private void createTabGroups() throws Exception {
		statement.execute("DROP TABLE IF EXISTS Groups;");
		statement.execute("CREATE TABLE Groups ("
				+ "groupName VARCHAR(10),"
				+ "creater VARCHAR(20),"
				+ "PRIMARY KEY(groupName));");
		statement.execute("INSERT INTO Groups VALUES(\"TonyG1\", \"Tony\");");
		statement.execute("INSERT INTO Groups VALUES(\"TonyG2\", \"Tony\");");
		statement.execute("INSERT INTO Groups VALUES(\"TonyG3\", \"Tony\");");
		statement.execute("INSERT INTO Groups VALUES(\"ElonG1\", \"Elon\");");
		statement.execute("INSERT INTO Groups VALUES(\"ElonG2\", \"Elon\");");
	}
	

	private void createTabFriends() throws Exception {
		statement.execute("DROP TABLE IF EXISTS Friends;");
		statement.execute("CREATE TABLE Friends ("
				+ "username VARCHAR(10),"
				+ "friendsName VARCHAR(10));");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Stark\");");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Elon\");");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Musk\");");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Musk1\");");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Musk3\");");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Musk4\");");
		statement.execute("INSERT INTO Friends VALUES(\"Tony\", \"Musk6\");");
		statement.execute("INSERT INTO Friends VALUES(\"Stark\", \"Tony\");");
		statement.execute("INSERT INTO Friends VALUES(\"Elon\", \"Tony\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk\", \"Tony\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk1\", \"Tony\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk3\", \"Tony\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk4\", \"Tony\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk6\", \"Tony\");");

		statement.execute("INSERT INTO Friends VALUES(\"Elon\", \"Musk1\");");
		statement.execute("INSERT INTO Friends VALUES(\"Elon\", \"Musk2\");");
		statement.execute("INSERT INTO Friends VALUES(\"Elon\", \"Musk5\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk1\", \"Elon\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk2\", \"Elon\");");
		statement.execute("INSERT INTO Friends VALUES(\"Musk5\", \"Elon\");");

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
	
	
	private void createTabUsers() throws Exception {
		statement.execute("DROP TABLE IF EXISTS Users;");	//如果用户表表存在就直接退出，不存在就建表
		statement.execute("CREATE TABLE Users ("			//建表，并给表添加数据项
				+ "username VARCHAR(10),"
				+ "password VARCHAR(20),"
				+ "PRIMARY KEY(username))");
		/*主关键字(primary key)是表中的一个或多个字段，它的值用于唯一地标识表中的某一条记录。在两个表的
		关系中，主关键字用来在一个表中引用来自于另一个表中的特定记录。主关键字是一种唯一关键字，表定义的一部分。
		一个表的主键可以由多个关键字
		共同组成，并且主关键字的列不能包含空值。主关键字是可选的，并且可在 CREATE TABLE 或 ALTER TABLE 语句中定义。 */
		//插入数据
		statement.execute("INSERT INTO Users VALUES(\"Tony\", \"12345tony\");");
		statement.execute("INSERT INTO Users VALUES(\"mark\", \"1202\");");
		statement.execute("INSERT INTO Users VALUES(\"Elon\", \"12345elon\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk\", \"12345musk\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk1\", \"12345musk1\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk2\", \"12345musk2\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk3\", \"12345musk3\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk4\", \"12345musk4\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk5\", \"12345musk5\");");
		statement.execute("INSERT INTO Users VALUES(\"Musk6\", \"12345musk6\");");
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
