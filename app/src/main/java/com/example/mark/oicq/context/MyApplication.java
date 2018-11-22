package com.example.mark.oicq.context;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;
    private static Context activityContext;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
    public static Context getActivityContext(){return activityContext;}
    public static void setActivityContent(Context context){activityContext=context;}
}
