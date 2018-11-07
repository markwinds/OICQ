package com.example.mark.oicq.classes;

public class Message {
    public static final int MESSAGE_TYPE_RECEIVED=0;
    public static final int MESSAGE_TYPE_SEND=1;
    private String message;
    private int messageType;

    public Message(String message,int messageType){
        this.message=message;
        this.messageType=messageType;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getMessage() {
        return message;
    }
}
