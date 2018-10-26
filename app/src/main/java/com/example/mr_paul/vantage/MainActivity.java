package com.example.mr_paul.vantage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements
NewsFeed.OnFragmentInteractionListener, PostAd.OnFragmentInteractionListener, Profile.OnFragmentInteractionListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNetworkAvailable()){
            Toast.makeText(this, "Connect to the internet, and relaunch the app", Toast.LENGTH_LONG).show();
        }

        authenticateUser(); // this method authenticates the user using login credentials

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // adding new tabs in the tabLayout
        tabLayout.addTab(tabLayout.newTab().setText("News Feed"));
        tabLayout.addTab(tabLayout.newTab().setText("Post Ad"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // referencing to the ViewPager and setting PageAdapter
        final ViewPager viewPager = findViewById(R.id.view_pager);
        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){}
            @Override
            public void onTabReselected(TabLayout.Tab tab){}
        });
    }

    public void authenticateUser(){

        final SharedPreferences sharedPreferences = getSharedPreferences("userInformation",
                MODE_PRIVATE);

        // authenticate the user every time the main activity is created
        AuthCredential credential = EmailAuthProvider
                .getCredential(sharedPreferences.getString("userEmail",""),
                        sharedPreferences.getString("password",""));
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.reauthenticate(credential);
        }

    }

    // query for internet availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // this is required to implement the OnFragmentInteractionListener
    @Override
    public void onFragmentInteraction(Uri uri){}
}

