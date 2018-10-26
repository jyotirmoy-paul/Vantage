package com.example.mr_paul.vantage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ShowAllChatsActivity extends AppCompatActivity {

    LinearLayout preLoading;
    ListView listView;

    // firebase variables
    DatabaseReference databaseReference;

    // other variables
    ArrayList<ChatInfoData> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_chats);

        // referencing to the views
        preLoading = findViewById(R.id.pre_loading);
        listView = findViewById(R.id.chat_list_view);

        SharedPreferences sharedPreferences = getSharedPreferences("userInformation",MODE_PRIVATE);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chatting Information")
                .child(sharedPreferences.getString("userUID","")).child("ChatInfo");

        arrayList = new ArrayList<>();
        final ChatInfoDataAdapter chatInfoDataAdapter = new ChatInfoDataAdapter(getApplicationContext(),
                R.layout.chat_name_layout,arrayList);

        // set an on child event listener at this reference
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                arrayList.add(dataSnapshot.getValue(ChatInfoData.class));
                chatInfoDataAdapter.notifyDataSetChanged();

                if(listView.getVisibility() == View.GONE){
                    // make the list view visible and the text view gone
                    listView.setVisibility(View.VISIBLE);
                    preLoading.setVisibility(View.GONE);
                }

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        listView.setAdapter(chatInfoDataAdapter);

        // set on list click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ChatInfoData currentInfoData = arrayList.get(position);

                // create a serverData that only requires name, user uid and user key
                // this is required for sending intent to the chat activity
                ServerData intentServerData = new ServerData(currentInfoData.getName(),
                        "",currentInfoData.getUserUID(),"","",
                        "","","","",
                        currentInfoData.getUserKey(),"","");

                // call an intent sending the serverData
                Intent intent = new Intent(ShowAllChatsActivity.this,ChattingActivity.class);
                intent.putExtra("serverData",intentServerData);
                startActivity(intent);

            }
        });

    }
}
