package com.example.mr_paul.vantage;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyPostedAdActivity extends AppCompatActivity {

    FirebaseStorage firebaseStorage;
    DatabaseReference databaseReference;
    String userUID;
    ArrayList<ServerData> userAdList;
    ListView adListView;
    LinearLayout preLoading;
    ServerDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posted_ad);

        // referencing to the views
        adListView = findViewById(R.id.ad_list_view);
        preLoading = findViewById(R.id.pre_loading);

        // creating firebase storage reference, for deleting images
        firebaseStorage = FirebaseStorage.getInstance();

        // creating database reference for listening to the advertisement data section
        databaseReference = FirebaseDatabase
                .getInstance().getReference().child("Advertisement Data");

        userUID = getSharedPreferences("userInformation", MODE_PRIVATE)
                .getString("userUID", "");

        userAdList = new ArrayList<>();
        adapter = new ServerDataAdapter(getApplicationContext(),
                R.layout.self_posted_ad_layout, userAdList);


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ServerData serverData = dataSnapshot.getValue(ServerData.class);
                String adDataUID = serverData.getUserUID();

                if (adDataUID.equals(userUID)) {
                    // then this ad was posted by this user
                    userAdList.add(serverData);
                    adapter.notifyDataSetChanged();
                }

                if (adListView.getVisibility() == View.GONE) {
                    // make the list view visible and make the text view gone
                    adListView.setVisibility(View.VISIBLE);
                    preLoading.setVisibility(View.GONE);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                if (userAdList.isEmpty()) {
                    adListView.setVisibility(View.GONE);
                    preLoading.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adListView.setAdapter(adapter);


        // implementing long click on item to delete the ad
        adListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                // show an dialog box, which will have the option to delete the add or confirm a deal
                // on both case, the ad gets deleted!
                String[] options = {"Mark as sold, and delete the Ad",
                        "Not interested anymore, delete the Ad"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPostedAdActivity.this);
                builder.setTitle("What do you want to do?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            // a deal has been confirmed by the user
                            Toast.makeText(MyPostedAdActivity.this,
                                    "Congrats! Now your ad will be removed!",
                                    Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences("userInformation", MODE_PRIVATE);
                            int numOfDealsConfirmed = sharedPreferences.getInt("numberOfConfirmedDeals", 0);

                            // write new data
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("numberOfConfirmedDeals", numOfDealsConfirmed + 1);
                            editor.apply();

                            deleteFromFirebase(position);
                        } else {
                            // the user wants to delete the ad
                            Toast.makeText(MyPostedAdActivity.this,
                                    "Your Ad is scheduled to be deleted!", Toast.LENGTH_SHORT).show();
                            deleteFromFirebase(position);
                        }

                    }
                });
                builder.show();

                return true;
            }
        });
    }

    // this method, deletes the uploaded photos form the firebase storage and removes the child created in
    // firebase realtime database
    public void deleteFromFirebase(int position){

        ServerData adData = userAdList.get(position);

        firebaseStorage.getReferenceFromUrl(adData.getPhoto_01()).delete();

        if(adData.getPhoto_02().length() != 0){
            firebaseStorage.getReferenceFromUrl(adData.getPhoto_02()).delete();
        }

        if(adData.getPhoto_03().length() != 0){
            firebaseStorage.getReferenceFromUrl(adData.getPhoto_03()).delete();
        }

        // finally remove the child from firebase database
        DatabaseReference deleteDataRef = databaseReference.child(adData.getAdKey());
        deleteDataRef.removeValue();

        // reflect changes in the app
        userAdList.remove(position); // remove from the array list
        adapter.notifyDataSetChanged();

    }

}
