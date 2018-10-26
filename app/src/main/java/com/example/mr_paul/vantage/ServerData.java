package com.example.mr_paul.vantage;

import java.io.Serializable;

public class ServerData implements Serializable{

    public String userName;
    public String choice;
    public String userUID;
    public String adTitleText;
    public String price;
    public String adDescription;
    public String photo_01;
    public String photo_02;
    public String photo_03;
    public String userUniqueKey;
    public String date;
    public String adKey;


    public ServerData(){
        // empty constructor for Firebase
    }

    public ServerData(String name, String choice, String userUID, String adTitleText, String price,
                      String adDescription, String photo_01, String photo_02, String photo_03,
                      String userUniqueKey,String date,String adKey){

        this.userName = name;
        this.choice = choice;
        this.userUID = userUID;
        this.adTitleText = adTitleText;
        this.price = price;
        this.adDescription = adDescription;
        this.photo_01 = photo_01;
        this.photo_02 = photo_02;
        this.photo_03 = photo_03;
        this.userUniqueKey = userUniqueKey;
        this.date = date;
        this.adKey = adKey;
    }

    public String getUserName(){return userName;}
    public String getChoice(){return choice;}
    public String getUserUID(){return userUID;}
    public String getAdTitleText(){return adTitleText;}
    public String getPrice(){return price;}
    public String getAdDescription(){return adDescription;}
    public String getPhoto_01(){return photo_01;}
    public String getPhoto_02(){return photo_02;}
    public String getPhoto_03(){return photo_03;}
    public String getUserUniqueKey(){return userUniqueKey;}
    public String getDate(){return date;}
    public String getAdKey(){return adKey;}
    public int getNumOfPhotoAvailable(){
        if(photo_02.length() == 0){return 1;}
        else if(photo_03.length() == 0){return 2;}
        else{return 3;}
    }
}
