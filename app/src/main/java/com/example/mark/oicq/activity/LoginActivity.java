package com.example.mark.oicq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.mark.oicq.R;
import com.example.mark.oicq.adapter.SoftHideKeyBoardUtil;
import com.example.mark.oicq.classes.MyHandler;
import com.example.mark.oicq.context.ActivityManager;
import com.example.mark.oicq.context.MyApplication;
import com.example.mark.oicq.server.ServerManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends ActivityManager implements View.OnClickListener{

    private ServerManager serverManager = ServerManager.getServerManager();
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //隐藏导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //隐藏状态栏
        }

        MyHandler.getMyHandler();       //myHandler一定要在主线程中初始化
        new Thread(serverManager).start();
        SoftHideKeyBoardUtil.assistActivity(this);

        Button loginButton=findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        Button signinButton=findViewById(R.id.signin_button);
        signinButton.setOnClickListener(this);
        usernameInput=findViewById(R.id.username_input);
        passwordInput=findViewById(R.id.password_edit_text);
        checkBox=findViewById(R.id.check_box);

        loadData();

        //允许在主线程中进行耗时操作
//        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                String username = usernameInput.getText().toString();   //将输入框内的字符串读取出来
                String password = passwordInput.getText().toString();
                switch (login(username, password)){
                    case 0:{
                        usernameInput.setText("");
                        passwordInput.setText("");
                        Toast.makeText(LoginActivity.this, "username or password is wrong", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1:{
                        //--------在服务器端判断账号密码正确后就将用户状态置为上线-------
                        rememberMe();   //检测是否有勾选记住密码
                        serverManager.setUsername(username);    //设置该线程的用户名
                        HomePageActivity.setMyUsername(username);
                        Intent intent = new Intent(this, HomePageActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(LoginActivity.this, "Log in succeed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2:{
                        Toast.makeText(LoginActivity.this, "username or password is illegal", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 3:{
                        Toast.makeText(LoginActivity.this, "connect to server out of time", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }//switch
                break;
            }
            case R.id.signin_button: {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                int status=signin(username, password);
                if (status==1) {
                    HomePageActivity.setMyUsername(username);
                    Toast.makeText(LoginActivity.this, "Sign in succeed", Toast.LENGTH_SHORT).show();
                } else if(status==0){
                    usernameInput.setText("");
                    passwordInput.setText("");
                    Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                }else if(status==2){
                    Toast.makeText(LoginActivity.this, "The name has been used!!!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }


    //发起登录请求
    /*输入不合法则返回2
      服务器连接超时返回3
      密码错误返回0
      密码正确返回1*/
    private int login(String username, String password) {
        // check username and password whether legal
        if (username.equals("") || username.length() > 10 || password.length() > 20) {
            return 2;   //输入不合法则返回2
        }
        // send msg to servers
        String msg = "[LOGIN]:[" + username + ", " + password + "]";
        serverManager.sendMessage(this, msg);
        // get msg from servers return
        String ack = serverManager.getMessage();
        // deal msg
        if (ack == null) {
            return 3;
        }
        serverManager.setMessage(null);     //读取完服务器的信息后将接收区（message）清空
        String p = "\\[ACKLOGIN\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(ack);
        if(matcher.find() && matcher.group(1).equals("1")){
            return 1;
        }
        return 0;
    }


    private int signin(String username, String password) {
        // check username and password whether legal
        if (username == null || username.length() > 10 || password.length() > 20) {
            return 0;
        }
        // send msg to servers
        String msg = "[REGISTER]:[" + username + ", " + password + "]";
        serverManager.sendMessage(this, msg);
        // get msg from servers return
        String ack = serverManager.getMessage();
        // deal msg
        if (ack == null) {
            return 0;
        }
        serverManager.setMessage(null);
        String p = "\\[ACKREGISTER\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(ack);
        if(!matcher.find()){
            return 0;
        }
        if(matcher.group(1).equals("1")){
            return 1;
        }
        if(matcher.group(1).equals("2")){
            return 2;
        }
        return 0;
    }


    /*进入登录界面后将记住的用户名和密码填入*/
    public void loadData(){
        SharedPreferences pre=getSharedPreferences("remember",MODE_PRIVATE);
        String username=pre.getString("username","");
        String password=pre.getString("password","");
        boolean checkbox=pre.getBoolean("checkbox",false);
        checkBox.setChecked(checkbox);     //将读入写勾选状态显示出来
        if(checkbox){       //如果上次已经选择记住密码
            usernameInput.setText(username);    //将存储的用户名填入
            passwordInput.setText(password);
        }
    }


    /*在验证密码成功以后检查是否勾选记住密码，如果勾选则将密码写入存储在下次登录的时候用*/
    public void rememberMe(){
        boolean checkbox=checkBox.isChecked();
        SharedPreferences .Editor remember = getSharedPreferences("remember",MODE_PRIVATE).edit();
        remember.putBoolean("checkbox",checkbox);   //更新勾选状态
        remember.apply();
        if(checkbox){
            remember.putString("username",usernameInput.getText().toString());  //如果选择记住用户名则更新用户名
            remember.putString("password",passwordInput.getText().toString());
            remember.apply();
        }
    }
}
