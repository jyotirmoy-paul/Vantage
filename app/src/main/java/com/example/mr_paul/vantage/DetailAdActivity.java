package com.example.mr_paul.vantage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;

public class DetailAdActivity extends AppCompatActivity {

    ImageView coverImageView;
    TextView numOfPhotos;
    TextView userName;
    TextView dateOfAdPosting;
    TextView choiceOfAd;
    TextView priceOfProduct;
    TextView adTitle;
    TextView adDescription;
    LinearLayout viewPhotos;
    LinearLayout chat;
    View separatingView;

    ServerData userServerData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_ad);

        // get the server data from the intent
        Intent intent = getIntent();
        userServerData = (ServerData) intent.getSerializableExtra("serverData");

        // referencing to the views
        coverImageView = findViewById(R.id.cover_image);
        numOfPhotos = findViewById(R.id.num_of_photos);
        userName = findViewById(R.id.user_name);
        dateOfAdPosting = findViewById(R.id.date_of_ad_posting);
        choiceOfAd = findViewById(R.id.choice);
        priceOfProduct = findViewById(R.id.price);
        adTitle = findViewById(R.id.ad_title);
        adDescription = findViewById(R.id.ad_description);
        viewPhotos = findViewById(R.id.view_photos);
        chat = findViewById(R.id.chat);
        separatingView = findViewById(R.id.sep_view);

        // load the cover image
        Glide.with(getApplicationContext()).load(Uri.parse(userServerData.getPhoto_01())).into(coverImageView);

        // show the number of photos
        String photoNums = userServerData.getNumOfPhotoAvailable() + " Photos";
        numOfPhotos.setText(photoNums);

        // disalaying username
        if(getSharedPreferences("userInformation", Context.MODE_PRIVATE)
                .getString("userUID","").equals(userServerData.getUserUID())){
            userName.setText("Me");
        }
        else{
            userName.setText(userServerData.getUserName());
        }

        //show the date of ad posting
        String date = "Ad posted on " + userServerData.getDate().trim();
        dateOfAdPosting.setText(date);

        // show choice of ad and price
        String choice = userServerData.getChoice();
        String price = userServerData.getPrice();
        if(price.length() != 0){
            // then ad for sale
            DecimalFormat decimalFormat = new DecimalFormat("##,##,##,###");
            price = decimalFormat.format(Integer.parseInt(price));
            price = "\u20B9 " + price;
            priceOfProduct.setText(price);
        }
        else{
            priceOfProduct.setText("-----");
        }
        choiceOfAd.setText(choice);

        // show ad Title
        adTitle.setText(userServerData.getAdTitleText());

        // show ad Description
        adDescription.setText(userServerData.getAdDescription());

        // if the user himself is visiting, then remove the chat functionality
        String thisUserUID = getSharedPreferences("userInformation",MODE_PRIVATE).getString("userUID","");
        if(thisUserUID.equals(userServerData.getUserUID())){
            // disable the chat functionality
            chat.setVisibility(View.GONE);
            separatingView.setVisibility(View.GONE);
        }
        else{
            // set on click listener to call an intent to chatActivity
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DetailAdActivity.this,ChattingActivity.class);
                    intent.putExtra("serverData",userServerData);
                    startActivity(intent);
                }
            });

        }

        viewPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call an intent to view image activity, to view other images uploaded by the user, if available
                Pair<View,String> p1 = new Pair<>((View)coverImageView,"adCoverTransition");
                ActivityOptionsCompat optionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                DetailAdActivity.this,p1);
                Intent intent = new Intent(DetailAdActivity.this,ImageViewActivity.class);
                intent.putExtra("userServerData",userServerData);
                startActivity(intent,optionsCompat.toBundle());
            }
        });

    }
}
