package com.example.mark.oicq.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mark.oicq.R;
import com.example.mark.oicq.adapter.FriendAdapter;
import com.example.mark.oicq.classes.Friend;
import com.example.mark.oicq.classes.MyDatabaseHelper;
import com.example.mark.oicq.context.MyApplication;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity {

    //private Friend friend=new Friend(R.drawable.profile_big,"Lucy");
    private static String myUsername;
    private static List<Friend> friendList=new ArrayList<>();
    private SwipeRefreshLayout homeRefresh;
    private FloatingActionButton floatingActionButton;
    private static RecyclerView homeRecyclerView;
    private static FriendAdapter friendAdapter;
    private MyDatabaseHelper dbhelper;
    private static SQLiteDatabase db;
    private static Cursor cursor;
    private static ContentValues values=new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        MyApplication.setActivityContent(HomePageActivity.this);

        final DrawerLayout mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ImageView homeProfile=findViewById(R.id.home_profile);
        homeRecyclerView=findViewById(R.id.home_recycle_view);
        homeRefresh=findViewById(R.id.home_refresh);
        floatingActionButton=findViewById(R.id.add_button);
        dbhelper=MyDatabaseHelper.getMyDatabaseHelper();
        db=dbhelper.getWritableDatabase();

        friendList.clear();
//        for(int i=0;i<50;i++){
//            friendList.add(friend);
//        }

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        homeRecyclerView.setLayoutManager(layoutManager);
        friendAdapter=new FriendAdapter(friendList);
        homeRecyclerView.setAdapter(friendAdapter);
        homeRefresh.setColorSchemeResources(R.color.colorPrimary);

        //initData();

        homeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        homeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //在这里添加主页刷新的逻辑
                homeRefresh.setRefreshing(false);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });
    }//create

    public static List<Friend> getFriendList(){
        return friendList;
    }

    public static void addFriend(String friend){
        friendList.add(new Friend(R.drawable.profile_big,friend));
        friendAdapter.notifyItemChanged(friendList.size()-1);
        values.clear();
        values.put("host",myUsername);
        values.put("friend",friend);
        db.insert("friends",null,values);
    }

    public void initData(){
//        String[] columns = new  String[] {"friend"};
//        String[] selectionArgs = new String[]{getMyUsername()};
//        cursor=db.query("friends",columns,"host=?",selectionArgs,null,null,null);
        cursor=db.rawQuery("select friend from friends where host = '"+getMyUsername()+"';",null);
        if(cursor.moveToFirst()){
            do{
                friendList.add(new Friend(R.drawable.profile_big,cursor.getString(cursor.getColumnIndex("friend"))));
                friendAdapter.notifyItemChanged(friendList.size());
            }while (cursor.moveToNext());
        }
    }

    public static String getMyUsername(){
        return myUsername;
    }

    public static void setMyUsername(String name){
        myUsername=name;
    }
}
