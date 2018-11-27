package com.example.mark.oicq.classes;

import java.util.ArrayList;
import java.util.List;

public class ChatList {
    private String friendName;
    private List<Message> messageList=new ArrayList<>();

    public ChatList(String name){
        friendName=name;
    }

    public String getFriendName(){
        return friendName;
    }

    public List<Message> getMessageList(){
        return messageList;
    }

    public void setMessageList(List<Message> mList){
        messageList=mList;
    }


}
