package com.example.mark.oicq.activity;

import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mark.oicq.R;
import com.example.mark.oicq.adapter.GuidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private static final int PICTURE_NUM=3;                                 //图片的数量
    private int[] pointId={R.id.iv_point1,R.id.iv_point2,R.id.iv_point3};   //进度点对应的控件id
    private List<ImageView> points=new ArrayList<ImageView>();              //存储进度点的实例
    private List<View> viewList= new ArrayList<>();                         //用来存储viewPage每一页的布局
    private int lastlocation=0;                                             //记录上一次是第几张图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);   //隐藏导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);       //隐藏状态栏
        }
        //getSupportActionBar().hide();       //隐藏actionbar

        //------------------将进度点与对应的控件匹配-----------------
        for(int i=0;i<PICTURE_NUM;i++){
            points.add((ImageView)findViewById(pointId[i]));
        }

        //-----------将布局转化为View控件,为adapter提供数组数据------------
        LayoutInflater inflater = LayoutInflater.from(this);
        viewList.add(inflater.inflate(R.layout.guide_page1, null));
        viewList.add(inflater.inflate(R.layout.guide_page2, null));
        viewList.add(inflater.inflate(R.layout.guide_page3, null));

        //---------------------------配置适配器-----------------
        GuidePagerAdapter guidePagerAdapter = new GuidePagerAdapter(this, viewList);
        ViewPager viewPager=findViewById(R.id.vp_guide);
        viewPager.setAdapter(guidePagerAdapter);
        viewPager.addOnPageChangeListener(this);

        Button button = (Button) (viewList.get(PICTURE_NUM-1)).findViewById(R.id.goto_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }//onCreate


    /*onPageSelected(int arg0) ：   此方法是页面跳转完后得到调用，arg0是你当前选中的页面的Position（位置编号）。*/
    @Override
    public void onPageSelected(int i) {
        points.get(lastlocation).setImageResource(R.drawable.unselected);
        points.get(i).setImageResource(R.drawable.selected);
        lastlocation=i;
    }

    /*onPageScrolled(int arg0,float arg1,int arg2)    ，当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到
    调用。其中三个参数的含义分别为：
    arg0 :当前页面，及你点击滑动的页面
    arg1:当前页面偏移的百分比
    arg2:当前页面偏移的像素位置 */
    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /*onPageScrollStateChanged(int arg0)   ，此方法是在状态改变的时候调用，其中arg0这个参数
    有三种状态（0，1，2）。arg0 ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
    当页面开始滑动的时候，三种状态的变化顺序为（1，2，0）*/
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
}
