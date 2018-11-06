package com.example.mark.oicq.activity;

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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomePageActivity extends AppCompatActivity {

    private Friend friend=new Friend(R.drawable.profile_big,"Lucy");
    private List<Friend> friendList=new ArrayList<>();
    private SwipeRefreshLayout homeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        final DrawerLayout mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ImageView homeProfile=findViewById(R.id.home_profile);
        RecyclerView homeRecyclerView=findViewById(R.id.home_recycle_view);
        homeRefresh=findViewById(R.id.home_refresh);

        friendList.clear();
        for(int i=0;i<50;i++){
            friendList.add(friend);
        }

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        homeRecyclerView.setLayoutManager(layoutManager);
        FriendAdapter friendAdapter=new FriendAdapter(friendList);
        homeRecyclerView.setAdapter(friendAdapter);
        homeRefresh.setColorSchemeResources(R.color.colorPrimary);


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


    }

}
