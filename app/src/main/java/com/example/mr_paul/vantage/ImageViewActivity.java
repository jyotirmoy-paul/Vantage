package com.example.mr_paul.vantage;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    ImageView prevImage;
    ImageView nextImage;
    ImageView closeImageView;
    ImageView mainImageView;
    String currentImageUri;
    String photo_01;
    String photo_02;
    String photo_03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        prevImage = findViewById(R.id.prev_image);
        nextImage = findViewById(R.id.next_image);
        closeImageView = findViewById(R.id.exit_image_view);
        mainImageView = findViewById(R.id.main_image_view);

        Intent intent = getIntent();
        ServerData serverData = (ServerData) intent.getSerializableExtra("userServerData");

        photo_01 = serverData.getPhoto_01();
        photo_02 = serverData.getPhoto_02();
        photo_03 = serverData.getPhoto_03();

        currentImageUri = photo_01;
        Glide.with(getApplicationContext()).load(Uri.parse(currentImageUri)).into(mainImageView);


        photo_02 = serverData.getPhoto_02();
        photo_03 = serverData.getPhoto_03();

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // quit the image view
                ImageViewActivity.this.finish();
            }
        });

    }

    public void nextImage(View view){

        if(currentImageUri.equals(photo_01) && photo_02.length() != 0){
            currentImageUri = photo_02;
            Glide.with(getApplicationContext()).load(Uri.parse(currentImageUri)).into(mainImageView);
        }

        else if(currentImageUri.equals(photo_02) && photo_03.length() != 0){
            currentImageUri = photo_03;
            Glide.with(getApplicationContext()).load(Uri.parse(currentImageUri)).into(mainImageView);
        }

        else{
            Toast.makeText(this,
                    "End of Image", Toast.LENGTH_SHORT).show();
            // play the no way sound
            if(getSharedPreferences("settings",MODE_PRIVATE)
                    .getBoolean("playsound",true)){
                MediaPlayer.create(ImageViewActivity.this,R.raw.no_way).start();
            }
        }

    }

    public void previousImage(View view){

        if(currentImageUri.equals(photo_03)){
            currentImageUri = photo_02;
            Glide.with(getApplicationContext()).load(Uri.parse(currentImageUri)).into(mainImageView);
        }
        else if(currentImageUri.equals(photo_02)){
            currentImageUri = photo_01;
            Glide.with(getApplicationContext()).load(Uri.parse(currentImageUri)).into(mainImageView);
        }
        else{
            Toast.makeText(this,
                    "End of Image", Toast.LENGTH_SHORT).show();
            // play the no way sound
            if(getSharedPreferences("settings",MODE_PRIVATE)
                    .getBoolean("playsound",true)){
                MediaPlayer.create(ImageViewActivity.this,R.raw.no_way).start();
            }
        }

    }

}
