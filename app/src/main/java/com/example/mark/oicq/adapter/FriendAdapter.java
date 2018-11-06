package com.example.mark.oicq.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mark.oicq.R;
import com.example.mark.oicq.classes.Friend;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context mContext;
    private List<Friend> mFriendList;

    //将布局中的控件都找出来放在java的变量中方便往控件中赋值
    static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView textView;
        public ViewHolder(View view){
            super(view);
            circleImageView=view.findViewById(R.id.friend_profile);
            textView=view.findViewById(R.id.friend_name);
        }
    }

    public FriendAdapter(List<Friend> friendList){
        mFriendList=friendList;
    }

    //给对应的控件赋值
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Friend friend=mFriendList.get(i);
        viewHolder.textView.setText(friend.getFriendName());
        Glide.with(mContext).load(friend.getProfile()).into(viewHolder.circleImageView);
    }

    //利用布局生成相应的view实例
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mContext==null) mContext=viewGroup.getContext();
        View view=LayoutInflater.from(mContext).inflate(R.layout.friend_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }
}
