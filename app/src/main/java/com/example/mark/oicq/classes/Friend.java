package com.example.mark.oicq.classes;

public class Friend {
    private int profile;
    private String friendName;

    public Friend(int profile,String friendName) {
        this.profile=profile;
        this.friendName=friendName;
    }

    public int getProfile() {
        return profile;
    }

    public String getFriendName(){
        return friendName;
    }
}
