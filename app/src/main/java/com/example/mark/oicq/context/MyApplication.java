package com.example.mark.oicq.context;

import android.app.Application;
import android.content.Context;

import com.example.mark.oicq.classes.ChatList;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static Context context;
    //private static Context activityContext;
    private static List<Context> contextLists=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
    public static Context getActivityContext(){
        return contextLists.get(contextLists.size()-1);
    }
    //public static void setActivityContent(Context context){activityContext=context;}


    public static void addContext(Context mContext){
        contextLists.add(mContext);
    }

    public static void delContext(){
        contextLists.remove(contextLists.size()-1);
    }
}
