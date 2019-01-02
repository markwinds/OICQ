package com.example.mark.oicq.context;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class ActivityManager extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.addContext(this);
    }

    @Override
    public void finish() {
        super.finish();
        MyApplication.delContext();
    }
}
