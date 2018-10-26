package com.example.mr_paul.vantage;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class ChattingActivity extends AppCompatActivity {

    // get user information
    String uid;
    String userKey;
    String userName;
    String chatKey;
    String selfUserKey;
    String selfUserUID;
    String selfUserName;
    ServerData serverData;

    // Firebase variables
    DatabaseReference databaseReference;

    // widgets variable
    TextView userNameTextView;
    ImageView exitChat;
    ListView chatListView;
    EditText writeMessageBox;
    Button sendMessageButton;
    DatabaseReference storeChatUserInfoRef;

    // variables for chat functionality to work
    ArrayList<ChatData> chatDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chating);


        // referencing to the views
        userNameTextView = findViewById(R.id.other_user_name);
        exitChat = findViewById(R.id.exit_chat);
        chatListView = findViewById(R.id.chat_list_view);
        writeMessageBox = findViewById(R.id.write_message);
        sendMessageButton = findViewById(R.id.send_button);

        chatDataList = new ArrayList<>();
        final ChatDataAdapter adapter = new ChatDataAdapter(getApplicationContext(),
                R.layout.message_item_layout,chatDataList);

        Intent intent = getIntent();
        serverData = (ServerData) intent.getSerializableExtra("serverData");

        // get the required user information
        uid = serverData.getUserUID();
        userKey = serverData.getUserUniqueKey();
        userName = serverData.getUserName();

        // set the user name
        userNameTextView.setText(userName);

        selfUserKey = getSharedPreferences("userInformation",MODE_PRIVATE)
                .getString("userUniqueKey","");
        selfUserUID = getSharedPreferences("userInformation",MODE_PRIVATE)
                .getString("userUID","");
        selfUserName = getSharedPreferences("userInformation",MODE_PRIVATE)
                .getString("userName","");

        // chat key creation ---> (a + b) + (a * b)
        chatKey = add(multiply(userKey,selfUserKey),add(userKey,selfUserKey));


        // a unique room is created common for both the user, they can chat here
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Chatting Data").child(chatKey);


        // this code is required for the notification to work properly
        Boolean requireToCall = getSharedPreferences("userInformation",MODE_PRIVATE)
                .getBoolean("requireToCall",true);
        if(requireToCall){
            // this code snippet is needed to be called only once
            storeChatUserInfoRef = FirebaseDatabase.getInstance().getReference()
                    .child("Chatting Information"); // push and store the other user UID
            storeChatUserInfoRef.child(uid).child("isThereANewMessage").setValue(-1);

            getSharedPreferences("userInformation",MODE_PRIVATE)
                    .edit().putBoolean("requireToCall",false).apply();
        }


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String message = writeMessageBox.getText().toString().trim();

                if(message.length() == 0){
                    Toast.makeText(ChattingActivity.this,
                            "Can't send empty message!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(message.length() > 512){
                    Toast.makeText(ChattingActivity.this,
                            "Max 512 chars available", Toast.LENGTH_SHORT).show();
                    return;
                }

                // play the send notification sound ----> by default play the sound
                if(getSharedPreferences("settings",MODE_PRIVATE)
                        .getBoolean("playsound",true)){
                    MediaPlayer.create(ChattingActivity.this,R.raw.send_message).start();
                }

                writeMessageBox.setText("");

                // this is helps in notification creation also!
                storeChatInfo();

                // getting and formatting the date object
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM");

                // finally uploading to the server
                ChatData chatData = new ChatData(message,dateFormat.format(date),selfUserUID);
                databaseReference.push().setValue(chatData);
            }
        });

        // set on child event listener on databaseReference
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ChatData newChatData = dataSnapshot.getValue(ChatData.class);
                chatDataList.add(newChatData);
                adapter.notifyDataSetChanged();

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

        chatListView.setAdapter(adapter);

        // onClickListener for showing date option
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String timeAndDateView = chatDataList.get(position).getTimeAndDate();
                Toast.makeText(ChattingActivity.this,
                        timeAndDateView, Toast.LENGTH_SHORT).show();
            }
        });

        // exitChat functionality
        exitChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChattingActivity.this.finish();
            }
        });
    }


    public String add(String num1, String num2) {
        int carry = 0;
        int m = num1.length(), n = num2.length();
        int len = m < n ? n : m;
        char[] res = new char[len + 1]; // length is maxLen + 1 in case of carry in adding most significant digits
        for(int i = 0; i <= len ; i++) {
            int a = i < m ? (num1.charAt(m - i - 1) - '0') : 0;
            int b = i < n ? (num2.charAt(n - i - 1) - '0') : 0;
            res[len - i] = (char)((a + b + carry) % 10 + '0');
            carry = (a + b + carry) / 10;
        }
        return res[0] == '0' ? new String(res, 1, len) : new String(res, 0, len + 1);
    }


    // utility function to multiply two very large numbers using string
    public static String multiply(String num1, String num2) {
        String n1 = new StringBuilder(num1).reverse().toString();
        String n2 = new StringBuilder(num2).reverse().toString();
        int[] d = new int[num1.length()+num2.length()];

        //multiply each digit and sum at the corresponding positions
        for(int i=0; i<n1.length(); i++){
            for(int j=0; j<n2.length(); j++){
                d[i+j] += (n1.charAt(i)-'0') * (n2.charAt(j)-'0');
            }
        }

        StringBuilder sb = new StringBuilder();

        //calculate each digit
        for(int i=0; i<d.length; i++){
            int mod = d[i]%10;
            int carry = d[i]/10;
            if(i+1<d.length){
                d[i+1] += carry;
            }
            sb.insert(0, mod);
        }
        //remove front 0's
        while(sb.charAt(0) == '0' && sb.length()> 1){
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public void storeChatInfo(){

        /*
         * This information is stored, so that a user can fetch his/her chatting with any other user
         * we just need to fetch from "/Chatting Information/my uid/* "
         * */

        storeChatUserInfoRef = FirebaseDatabase.getInstance().getReference()
                .child("Chatting Information"); // push and store the other user UID

        // is there a new message is used by the notification builder, to notify the user about any new
        // message by changing a variable -----> this is required for only the other user (not self)
        Random random = new Random();
        storeChatUserInfoRef.child(uid).child("isThereANewMessage").setValue(random.nextLong());

        ChatInfoData userChatInfo = new ChatInfoData(userKey,userName,uid);
        ChatInfoData selfChatInfo = new ChatInfoData(selfUserKey,selfUserName,selfUserUID);

        storeChatUserInfoRef.child(selfUserUID).child("ChatInfo").child(uid).setValue(userChatInfo);
        storeChatUserInfoRef.child(uid).child("ChatInfo").child(selfUserUID).setValue(selfChatInfo);
    }

}
