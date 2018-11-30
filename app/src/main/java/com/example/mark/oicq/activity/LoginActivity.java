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
import com.example.mark.oicq.classes.MyHandler;
import com.example.mark.oicq.context.MyApplication;
import com.example.mark.oicq.server.ServerManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

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
        //getSupportActionBar().hide();       //隐藏actionbar
        MyHandler.getMyHandler();       //myHandler一定要在主线程中初始化
        serverManager.start();      //开启线程


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
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (login(username, password)) {
                    //--------在服务器端判断账号密码正确后就将用户状态置为上线-------
                    rememberMe();
                    serverManager.setUsername(username);
                    HomePageActivity.setMyUsername(username);
                    Intent intent = new Intent(this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, "Log in succeed", Toast.LENGTH_SHORT).show();
                } else {
                    usernameInput.setText("");
                    passwordInput.setText("");
                    Toast.makeText(LoginActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                }
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
    private boolean login(String username, String password) {
        // check username and password whether legal
        if (username == null || username.length() > 10 || password.length() > 20) {
            return false;
        }
        // send msg to servers
        String msg = "[LOGIN]:[" + username + ", " + password + "]";

        serverManager.sendMessage(this, msg);

        // get msg from servers return
        String ack = serverManager.getMessage();
        // deal msg
        if (ack == null) {
            return false;
        }
        serverManager.setMessage(null);
        String p = "\\[ACKLOGIN\\]:\\[(.*)\\]";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(ack);
        return matcher.find() && matcher.group(1).equals("1");
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

    public void loadData(){
        SharedPreferences pre=getSharedPreferences("remember",MODE_PRIVATE);
        String username=pre.getString("username","");
        String password=pre.getString("password","");
        boolean checkbox=pre.getBoolean("checkbox",false);
        checkBox.setChecked(checkbox);
        if(checkbox){
            usernameInput.setText(username);
            passwordInput.setText(password);
        }
    }

    public void rememberMe(){
        boolean checkbox=checkBox.isChecked();
        SharedPreferences .Editor remember = getSharedPreferences("remember",MODE_PRIVATE).edit();
        remember.putBoolean("checkbox",checkbox);
        remember.apply();
        if(checkbox){
            remember.putString("username",usernameInput.getText().toString());
            remember.putString("password",passwordInput.getText().toString());
            remember.apply();
        }
    }

}
