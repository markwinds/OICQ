package com.example.mark.oicq.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mark.oicq.R;
import com.example.mark.oicq.context.MyApplication;
import com.example.mark.oicq.server.ServerManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddFriendActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText remarkInput;
    private ServerManager serverManager = ServerManager.getServerManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        MyApplication.setActivityContent(AddFriendActivity.this);   //顶层上下文

        usernameInput=findViewById(R.id.add_friend_name_text);
        remarkInput=findViewById(R.id.add_friend_remark_text);
        Button addFriendButton=findViewById(R.id.add_friend_button);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String remark = remarkInput.getText().toString();
                addFriend(username,remark);
            }
        });
    }//onCreate


    public void addFriend(String username, String remark){
        if (username == null || username.length() > 10 || remark.length() > 300) {
            Toast.makeText(AddFriendActivity.this, "The information input is illegal", Toast.LENGTH_SHORT).show();
            return;
        }
        if(HomePageActivity.haveFriend(username)){
            Toast.makeText(AddFriendActivity.this, username+" is already your friend!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String msg = "[ADDFRIEND]:[" + serverManager.getUsername() + ", "+ username + ", " + remark + "]";
        serverManager.sendMessage(this, msg);
        String ack = serverManager.getMessage();
        if (ack == null) {
            Toast.makeText(AddFriendActivity.this, "Connect to server failed!!", Toast.LENGTH_SHORT).show();
            return;
        }
        serverManager.setMessage(null);
        String p = "\\[ACKADDFRIEND\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(ack);
        if(!matcher.find()){
            Toast.makeText(AddFriendActivity.this, "Response of server read failed!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(matcher.group(1).equals("1")){
            Toast.makeText(AddFriendActivity.this, "Apply has been send", Toast.LENGTH_SHORT).show();
        }else if(matcher.group(1).equals("0")){
            Toast.makeText(AddFriendActivity.this, "Apply send failed", Toast.LENGTH_SHORT).show();
        }else if(matcher.group(1).equals("2")){
            Toast.makeText(AddFriendActivity.this, "You can not be friend with yourself!!", Toast.LENGTH_SHORT).show();
        }else if(matcher.group(1).equals("3")){
            Toast.makeText(AddFriendActivity.this, "Can not find user!!", Toast.LENGTH_SHORT).show();
        }
        return;
    }

}//AddFriendActivity






