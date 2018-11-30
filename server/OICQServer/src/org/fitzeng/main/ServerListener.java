package org.fitzeng.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//为什么用同一个手机发送请求会出现多个haha，是不是认为退出又登录了

//服务器监听客户端发出请求的程序,这个文件应该也不用改

public class ServerListener extends Thread{

	private ServerSocket serverSocket;

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(1116, 30);				//27777是服务器监听的端口，30队列长度
			while (true) {
				Socket socket = serverSocket.accept();				//当没有客户端发出请求是程序就会阻塞在这一句
				System.out.println("Receive socket request, begin to open a thread to server");							//当有客户端发送请求就在服务器控制台输出haha
				ChatSocket chatSocket = new ChatSocket(socket);		
				chatSocket.start();									//开启新的一个线程,来一个用户就开启一个线程
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
