package com.example.mr_paul.vantage;

import java.io.Serializable;

public class ChatInfoData implements Serializable{

    public String name;
    public String userKey;
    public String userUID;

    public ChatInfoData(){
        // empty constructor needed for firebase
    }

    public ChatInfoData(String userKey, String name,String userUID){
        this.name = name;
        this.userKey = userKey;
        this.userUID = userUID;
    }

    public String getName(){return name;}
    public String getUserKey(){return userKey;}
    public String getUserUID(){return userUID;}
}
