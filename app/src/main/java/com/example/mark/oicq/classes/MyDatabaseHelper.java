package com.example.mark.oicq.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mark.oicq.context.MyApplication;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //private Context mContext;
    public String sql;
    private static MyDatabaseHelper myDatabaseHelper=new MyDatabaseHelper(MyApplication.getContext(),"OICQ.db",null,1);

    public MyDatabaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        //mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sql="CREATE TABLE friends ("
                + "host VARCHAR(10),"
                + "friend VARCHAR(10));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static MyDatabaseHelper getMyDatabaseHelper(){
        return myDatabaseHelper;
    }
}
