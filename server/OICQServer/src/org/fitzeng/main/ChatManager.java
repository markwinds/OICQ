package org.fitzeng.main;

import java.util.ArrayList;
import java.util.List;

//管理多线程,这个文件不用改，直接用
public class ChatManager {

	private ChatManager(){};
	List<SocketMsg> socketList = new ArrayList<>();
	
	private static final ChatManager chatManager = new ChatManager();
	
	public static ChatManager getChatManager() {
		return chatManager;
	}
		
	public void add(SocketMsg cs) {
		socketList.add(cs);
	}
	
	public void remove(SocketMsg cs) {
		socketList.remove(cs);
	}

	public SocketMsg getSocketMsg(String username){			//根据用户名找到对应的SocketMsg
		for(int i=0;i<socketList.size();i++){
			if(socketList.get(i).getUsername().equals(username)) return socketList.get(i);
		}
		return null;
	}
	
}
