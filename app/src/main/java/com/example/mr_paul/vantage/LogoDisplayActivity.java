package com.example.mr_paul.vantage;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogoDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_display);

        new CountDownTimer(2000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // do nothing
            }
            @Override
            public void onFinish() {
                // call an intent to Main activity
                startActivity(new Intent(LogoDisplayActivity.this,MainActivity.class));
                LogoDisplayActivity.this.finish();
            }
        }.start();

    }
}
