package com.example.mark.oicq.activity;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.mark.oicq.R;
import com.example.mark.oicq.server.ServerManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private ServerManager serverManager = ServerManager.getServerManager();
    TextInputEditText usernameInput;
    TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //隐藏导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //隐藏状态栏
        }
        //getSupportActionBar().hide();       //隐藏actionbar
        serverManager.start();      //开启线程


        Button loginButton=findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        Button signinButton=findViewById(R.id.signin_button);
        signinButton.setOnClickListener(this);
        usernameInput=findViewById(R.id.username_input);
        passwordInput=findViewById(R.id.password_edit_text);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button: {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (login(username, password)) {
//                    serverManager.setUsername(username);
//                    Intent intent = new Intent(this, AtyMain.class);
//                    startActivity(intent);
//                    finish();
                    Toast.makeText(LoginActivity.this, "log in succeed", Toast.LENGTH_SHORT).show();
                } else {
//                    usernameInput.setText("");
//                    passwordInput.setText("");
                    Toast.makeText(LoginActivity.this, "log in failed", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.signin_button: {
//                Intent intent = new Intent(this, LoginActivity.class);
//                startActivity(intent);
//                finish();
                break;
            }
            default:
                break;
        }
    }


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




}
