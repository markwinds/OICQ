package com.example.mark.oicq.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.mark.oicq.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //隐藏导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //隐藏状态栏
        }
        //getSupportActionBar().hide();       //隐藏actionbar

    }
}
