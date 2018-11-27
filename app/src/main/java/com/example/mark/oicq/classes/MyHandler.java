package com.example.mark.oicq.classes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.mark.oicq.R;
import com.example.mark.oicq.activity.HomePageActivity;
import com.example.mark.oicq.activity.LoginActivity;
import com.example.mark.oicq.context.MyApplication;
import com.example.mark.oicq.server.ServerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyHandler extends Handler {

    private static MyHandler myHandler=new MyHandler();

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what){
            case 1:{            //当有好友申请时的UI操作
                String message=(String)msg.obj;
                String p = "\\[SERAPPLYFRIEND\\]:\\[(.*), (.*), (.*)\\]";
                Pattern pattern = Pattern.compile(p);
                Matcher matcher = pattern.matcher(message);
                final String origin,aim,remark;
                matcher.find();
                origin=matcher.group(1);            //在调用group之前必须先调用find方法
                aim=matcher.group(2);
                remark=matcher.group(3);

                AlertDialog addFriendDialog = new AlertDialog.Builder(MyApplication.getActivityContext())       //对话框不能用MyApplication.getContext()
                        .setTitle("Friend request")
                        .setMessage( origin + " want to make friend with you, withing remark: " + remark)
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String res = "[FEEDBACKFRIEND]:[" + aim + ", "+ origin + ", 1]";
                                ServerManager.getServerManager().sendMessage(MyApplication.getContext(),res);
                                String ack = ServerManager.getServerManager().getMessage();
                                if (ack == null) {
                                    Toast.makeText(MyApplication.getContext(), "Connect to server failed!!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ServerManager.getServerManager().setMessage(null);
                                String p = "\\[ACKFEEDBACKFRIEND\\]:\\[(.*)\\]";
                                Pattern pattern = Pattern.compile(p);
                                Matcher matcher = pattern.matcher(ack);
                                if(!matcher.find()){
                                    Toast.makeText(MyApplication.getContext(), "Response of server read failed!!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(matcher.group(1).equals("1")){
                                    Toast.makeText(MyApplication.getContext(), "You have agreed!", Toast.LENGTH_SHORT).show();
                                } else if(matcher.group(1).equals("0")) {
                                    Toast.makeText(MyApplication.getContext(), "Apply send failed", Toast.LENGTH_SHORT).show();
                                }
                                //------------------------这里添加更新本地数据的代码-----------------------
                                HomePageActivity.addFriend(origin);

                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String res = "[FEEDBACKFRIEND]:[" + aim + ", "+ origin + ", 0]";
                                ServerManager.getServerManager().sendMessage(MyApplication.getContext(),res);
                                String ack = ServerManager.getServerManager().getMessage();
                                if (ack == null) {
                                    Toast.makeText(MyApplication.getContext(), "Connect to server failed!!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ServerManager.getServerManager().setMessage(null);
                                String p = "\\[ACKFEEDBACKFRIEND\\]:\\[(.*)\\]";
                                Pattern pattern = Pattern.compile(p);
                                Matcher matcher = pattern.matcher(ack);
                                if(!matcher.find()){
                                    Toast.makeText(MyApplication.getContext(), "Response of server read failed!!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(matcher.group(1).equals("1")){
                                    Toast.makeText(MyApplication.getContext(), "You have confused!", Toast.LENGTH_SHORT).show();
                                } else if(matcher.group(1).equals("0")) {
                                    Toast.makeText(MyApplication.getContext(), "Apply send failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create();
                addFriendDialog.show();
                addFriendDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(15);
                addFriendDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(15);
                break;
            }
            case 2:{
                String message=(String)msg.obj;
                String p = "\\[SERRESPONDFRIEND\\]:\\[(.*), (.*), (.*)\\]";
                Pattern pattern = Pattern.compile(p);
                Matcher matcher = pattern.matcher(message);
                String result;
                final String origin,aim,status;
                matcher.find();
                origin=matcher.group(1);            //在调用group之前必须先调用find方法
                aim=matcher.group(2);
                status=matcher.group(3);
                if(status.equals("0")) result="confuse";
                else{
                    result="agree";
                    HomePageActivity.addFriend(origin);
                }
                //--------------弹窗--------------------
                AlertDialog addFriendDialog = new AlertDialog.Builder(MyApplication.getActivityContext())       //对话框不能用MyApplication.getContext()
                        .setTitle("Respond")
                        .setMessage( origin + " " + result + " to make friend with you!!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //------------------------这里添加更新本地数据的代码-----------------------
                            }
                        })
                        .create();
                addFriendDialog.show();
                addFriendDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(15);
                addFriendDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextSize(15);
                //Toast.makeText(MyApplication.getContext(), "You succeed!!!!", Toast.LENGTH_SHORT).show();
                break;
            }
            default: break;
        }
    }

    public static MyHandler getMyHandler(){
        return myHandler;
    }
}
