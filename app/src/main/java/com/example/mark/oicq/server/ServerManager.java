package com.example.mark.oicq.server;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerManager extends Thread {
    //private static final String IP = "192.168.70.41";			//这里填写网络ip，如果不用付费的内网穿透，这里在每次开通内网穿透的时候都要改为对应ip
    private static final String IP = "192.168.1.4";
    private Socket socket;
    private String username;
    private int iconID;
    private String message = null;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ReceiveChatMsg receiveChatMsg;
    private static final ServerManager serverManager = new ServerManager();

    public static ServerManager getServerManager() {
        return serverManager;
    }

    private ServerManager() {
        receiveChatMsg = new ReceiveChatMsg();
    }

    public void run() {
        try {
            socket = new Socket(IP, 27777);			//ip用来确定主机，27777用来确定主机的端口
            bufferedReader =  new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            String m = null;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("-1")) {
                    m += line;
                } else {
                    Log.d("TAG", "receive : " + m);			//日志输出收到的信息
                    if (ParaseData.getAction(m).equals("GETCHATMSG")) {
                        receiveChatMsg.delChatMsg(m);
                    } else {
                        message = m;
                    }
                    m = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.e("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh","Thread is over!!!");
            try {
                bufferedWriter.close();
                bufferedReader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendMessage(Context context, String msg) {
        try {
            while (socket == null) ;
            if (bufferedWriter != null) {
//                Log.d("TAG", "send : " + msg);
                bufferedWriter.write(msg + "\n");
                bufferedWriter.flush();
                bufferedWriter.write("-1\n");
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //用来接收数据存放到message
    public String getMessage() {				//这里用来干什么的
        for (int i = 0; i < 5; i++) {
            if (message != null) {
                break;
            }
            try {
                Thread.sleep(500);				//这是等待服务器响应吗
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
