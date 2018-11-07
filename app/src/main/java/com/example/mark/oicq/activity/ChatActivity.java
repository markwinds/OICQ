package com.example.mark.oicq.activity;

import com.example.mark.oicq.classes.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mark.oicq.R;
import com.example.mark.oicq.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Message[] messages={
            new Message("Mark",0),
            new Message("Hi Lucy",1),
            new Message("haha",0),
            new Message("en",1)};
    private List<Message> messageList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView chatRecyclerView=findViewById(R.id.chat_recycle_view);

        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        chatRecyclerView.setLayoutManager(layoutManager);
        InitMessages();
        MessageAdapter messageAdapter=new MessageAdapter(messageList);
        chatRecyclerView.setAdapter(messageAdapter);
    }

    public void InitMessages(){
        for(int i=0;i<4;i++){
            messageList.add(messages[i]);
        }
    }
}
