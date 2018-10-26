package com.example.mr_paul.vantage;

import java.io.Serializable;

public class ChatData implements Serializable {

    public String message;
    public String timeAndDate;
    public String senderUid;

    public ChatData(){
        // empty constructor for Firebase
    }

    public ChatData(String message, String timeAndDate, String uid){
        this.message = message;
        this.timeAndDate = timeAndDate;
        this.senderUid = uid;
    }

    public String getMessage(){return message;}
    public String getTimeAndDate(){return timeAndDate;}
    public String getSenderUid(){return senderUid;}

}
