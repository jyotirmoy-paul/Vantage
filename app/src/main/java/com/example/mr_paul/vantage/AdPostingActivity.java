package com.example.mr_paul.vantage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdPostingActivity extends AppCompatActivity {

    // firebase variables
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceAd;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    // ad related variables
    String userName;
    String choice;
    String userUID;
    String adTitleText;
    String price;
    String AdDescription;
    Uri photo_01 = null;
    Uri photo_02 = null;
    Uri photo_03 = null;
    String userUniqueKey; // this variable is required for chatting functionality

    // utility variables
    String key;
    UploadTask uploadTask01, uploadTask02, uploadTask03;
    Boolean image_01_done = false;
    Boolean image_02_done = false;
    Boolean image_03_done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_posting);

        Intent intent = getIntent();
        choice = intent.getStringExtra("choice");
        userUID = intent.getStringExtra("userUID");
        adTitleText = intent.getStringExtra("adTitleText");
        price = intent.getStringExtra("price");
        AdDescription = intent.getStringExtra("description");
        photo_01 = Uri.parse(intent.getStringExtra("photo_01"));

        String temp = intent.getStringExtra("photo_02");
        if(temp.length() == 0){photo_02 = null;}
        else{photo_02 = Uri.parse(temp);}

        temp = intent.getStringExtra("photo_03");
        if(temp.length() == 0){photo_03 = null;}
        else{photo_03 = Uri.parse(temp);}

        userUniqueKey = intent.getStringExtra("userUniqueKey");
        userName = intent.getStringExtra("userName");


        // creating the firebase references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase
                .getReference().child("Advertisement Data"); // all advertisement are posted in one place
        databaseReferenceAd = databaseReference.push();
        key = databaseReferenceAd.getKey();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child(userUID).child(key);


        // following lines of code uploads the ad images to firebase storage

        // uploading the first image ----> main cover image
        uploadTask01 = storageReference.child("image01").putFile(photo_01);
        uploadTask01.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.child("image01").getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // finally store the download url of the image
                    photo_01 = task.getResult();
                    image_01_done = true;
                    if((photo_02 != null && image_02_done) && (photo_03 != null && image_03_done)){
                        uploadToServer();
                    }
                    else if((photo_02 != null && image_02_done) && photo_03 == null){
                        uploadToServer();
                    }
                    else if(photo_02 == null && photo_03 == null){
                        uploadToServer();
                    }
                }
            }
        });


        // uploading the second image if exists
        if(photo_02 != null){
            uploadTask02 = storageReference.child("image02").putFile(photo_02);
            uploadTask02.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.child("image02").getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // finally store the download url of the image
                        photo_02 = task.getResult();
                        image_02_done = true;
                        if(image_01_done && (photo_03 != null && image_03_done)){
                            uploadToServer();
                        }
                        else if(image_01_done && photo_03 == null){
                            uploadToServer();
                        }
                    }
                }
            });
        }

        if(photo_03 != null){
            uploadTask03 = storageReference.child("image03").putFile(photo_03);
            uploadTask03.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storageReference.child("image03").getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // finally store the download url of the image
                        photo_03 = task.getResult();
                        image_03_done = true;
                        if(image_01_done && image_02_done){
                            uploadToServer();
                        }
                    }
                }
            });
        }
    }

    // this method uploads the required file to the real time database
    public void uploadToServer(){
        String image_01 = photo_01.toString();
        String image_02="";
        String image_03="";
        if(photo_02 != null){image_02 = photo_02.toString();}
        if(photo_03 != null){image_03 = photo_03.toString();}

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.US);


        ServerData serverData = new ServerData(userName,choice,userUID,adTitleText,price,AdDescription,
                image_01,image_02,image_03,userUniqueKey,dateFormat.format(date),key);
        databaseReferenceAd.setValue(serverData);

        SharedPreferences sharedPreferences = getSharedPreferences("userInformation",MODE_PRIVATE);

        // increase the number of adPosted by one
        int numOfAdsPosted = sharedPreferences.getInt("numberOfAdsPosted",0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("numberOfAdsPosted",numOfAdsPosted + 1);
        editor.apply();

        // confirmation that upload has been completed!
        Toast.makeText(this,
                "Congrats, Your ad is live now!", Toast.LENGTH_LONG).show();

        startActivity(new Intent(AdPostingActivity.this,MainActivity.class));
        AdPostingActivity.this.finish();
    }

}
