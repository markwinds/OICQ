package com.example.mark.oicq.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mark.oicq.R;
import com.example.mark.oicq.classes.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessageList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftMessageLayout;
        LinearLayout rightMessageLayout;
        TextView leftMessage;
        TextView rightMessage;
        public ViewHolder(View view) {
            super(view);
            leftMessageLayout = (LinearLayout) view.findViewById(R.id.left_message_layout);
            rightMessageLayout = (LinearLayout) view.findViewById(R.id.right_message_layout);
            leftMessage = (TextView) view.findViewById(R.id.left_message);
            rightMessage = (TextView) view.findViewById(R.id.right_message);
        }
    }

    public MessageAdapter(List<Message> msgList) {
        mMessageList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        if (message.getMessageType() == Message.MESSAGE_TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftMessageLayout.setVisibility(View.VISIBLE);
            holder.rightMessageLayout.setVisibility(View.GONE);
            holder.leftMessage.setText(message.getMessage());
        } else if(message.getMessageType() == Message.MESSAGE_TYPE_SEND) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightMessageLayout.setVisibility(View.VISIBLE);
            holder.leftMessageLayout.setVisibility(View.GONE);
            holder.rightMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

}