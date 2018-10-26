package com.example.mark.oicq.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.mark.oicq.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final int DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //隐藏导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //隐藏状态栏
        }
        getSupportActionBar().hide();       //隐藏actionbar

        new Handler().postDelayed(new Runnable() {      //利用Handler完成延时跳转
            @Override
            public void run() {
                //------------------检测是否是第一次进入程序----------------------
                SharedPreferences sharedPreferences = getSharedPreferences("oicq", MODE_PRIVATE);
                boolean guide = sharedPreferences.getBoolean("guide", true);        //如果没找到guide则表示第一次进入
                if(guide){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("guide",false);
                    editor.apply();
                    Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);      //第一次进入是进入引导界面
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);      //直接进入登录页面
                    startActivity(intent);
                    finish();
                }
            }
        }, DELAY);
    }//onCreate
}

