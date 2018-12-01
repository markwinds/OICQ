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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerManager implements Runnable {
    //private static final String IP = "192.168.70.41";			//这里填写网络ip，如果不用付费的内网穿透，这里在每次开通内网穿透的时候都要改为对应ip
    private static final String IP = "120.79.11.227";
    private Socket socket;
    private String username;
    private int iconID;
    private String message = null;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static final ServerManager serverManager = new ServerManager();


    public static ServerManager getServerManager() {
        return serverManager;
    }


    public void run() {
        try {
            socket = new Socket(IP, 1116);			//ip用来确定主机，27777用来确定主机的端口
            bufferedReader =  new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

            String m = null;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("-1")) {
                    if(m!=null){
                        m += line;
                    }else{
                        m=line;
                    }
                } else {
                    /*----------------------------服务器主动发起的请求统一分开处理，并且以SER开头--------------------------*/
                    if(getAction(m).equals("SERAPPLYFRIEND")){       //如果是服务器主动发起的好友申请
                        applyFriend(m);
                    } else if(getAction(m).equals("SERRESPONDFRIEND")){
                        respondFriend(m);
                    } else if(getAction(m).equals("SERMESSAGE")){
                        showMsg(m);
                        //Toast.makeText(MyApplication.getContext(), m, Toast.LENGTH_SHORT).show();
                    } else if(getAction(m).equals("SERUPDATAFRIEND")){
                        updateFriends(m);
                    } else if(getAction(m).equals("SERUPDATAMSG")){
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
        final String message=msg;   //这里必须声明为final
        new Thread(new Runnable() {
            @Override
            public void run() {     //因为该方法在主线程中被调用，且是耗时操作，所以这里开启一个新的线程来处理
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


    /*在客户端发送请求后即调用此方法，服务器发回的消息被线程自动存储在message中，
    该方法从message中读取信息并返回
    */
    public String getMessage() {
        for (int i = 0; i < 25; i++) {
            if (message != null) {
                break;
            }
            try {
                Thread.sleep(100);				//等待服务器响应时间，最长时间为25*100 2.5秒
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

    public String getAction(String msg) {
        String p = "\\[(.*)\\]:";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "error";
        }
    }


    public void claseAll(){
        try {
            bufferedWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //stopThreadFlag=true;
    }


    public void applyFriend(String msg){
        Message message=Message.obtain();
        message.what=MyHandler.APPLYFRIEND;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }


    public void respondFriend(String msg){
        Message message=Message.obtain();
        message.what=MyHandler.RESPONDFRIEND;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }


    public void showMsg(String msg){
        Message message=Message.obtain();
        message.what=MyHandler.SHOWMSG;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }


    public void updateFriends(String msg){
        Message message=Message.obtain();
        message.what=MyHandler.UPDATEFRIENDS;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }


    public void updateMsg(String msg){
        Message message=Message.obtain();
        message.what=MyHandler.UPDATAMSG;
        message.obj=msg;
        MyHandler.getMyHandler().sendMessage(message);
    }

}
