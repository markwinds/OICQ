package com.example.mark.oicq.server;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.mark.oicq.activity.AddFriendActivity;
import com.example.mark.oicq.activity.LoginActivity;
import com.example.mark.oicq.classes.MyHandler;
import com.example.mark.oicq.context.MyApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerManager extends Thread {
    //private static final String IP = "192.168.70.41";			//这里填写网络ip，如果不用付费的内网穿透，这里在每次开通内网穿透的时候都要改为对应ip
    //private static final String IP = "192.168.1.4";
    //private static final String IP = "http://u3wgnp.natappfree.cc";
    private static final String IP = "120.79.11.227";
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
            socket = new Socket(IP, 1116);			//ip用来确定主机，27777用来确定主机的端口
            bufferedReader =  new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            String m = null;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //Log.e("lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll","线程被调用");
                if (!line.equals("-1")) {
                    m += line;
                } else {
                    if (ParaseData.getAction(m).equals("GETCHATMSG")) {
                        receiveChatMsg.delChatMsg(m);
                    } else if(ParaseData.getAction(m).equals("SERAPPLYFRIEND")){       //如果是服务器主动发起的好友申请
                        applyFriend(m);
                    } else if(ParaseData.getAction(m).equals("SERRESPONDFRIEND")){
                       // Log.e("lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll","收到信息");
                        respondFriend(m);
                    } else if(ParaseData.getAction(m).equals("SERMESSAGE")){
                        showMsg(m);
                        //Toast.makeText(MyApplication.getContext(), m, Toast.LENGTH_SHORT).show();
                    } else if(ParaseData.getAction(m).equals("SERUPDATAFRIEND")){
                        updateFriends(m);
                    } else if(ParaseData.getAction(m).equals("SERUPDATAMSG")){
                        updateMsg(m);
                    } else {
                        message = m;
                    }
                    m = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //Log.e("lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll","线程被over");
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
        final String message=msg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket == null) ;
                    if (bufferedWriter != null) {
                        bufferedWriter.write(message + "\n");
                        bufferedWriter.flush();
                        bufferedWriter.write("-1\n");
                        bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    public void applyFriend(String msg){
        Message message=Message.obtain();
        message.what=1;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }

    public void respondFriend(String msg){
        Message message=Message.obtain();
        message.what=2;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }

    public void showMsg(String msg){
        Message message=Message.obtain();
        message.what=3;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }

    public void updateFriends(String msg){
        Message message=Message.obtain();
        message.what=4;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }

    public void updateMsg(String msg){
        Message message=Message.obtain();
        message.what=5;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }

}
