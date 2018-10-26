package com.example.mr_paul.vantage;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChatDataAdapter extends ArrayAdapter<ChatData> {

    // the default constructor of the class
    public ChatDataAdapter(Context context, int resource, ArrayList<ChatData> arrayList){
        super(context,resource,arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // recycling not needed!
        convertView = layoutInflater.inflate(R.layout.message_item_layout,parent,false);


       // finding and referencing to the views
        CardView selfMessageView = convertView.findViewById(R.id.self_message_view);
        CardView userMessageView = convertView.findViewById(R.id.user_message_view);

        TextView selfMessage = convertView.findViewById(R.id.self_message);
        TextView userMessage = convertView.findViewById(R.id.user_message);

        ChatData chatData = getItem(position);

        String uid = chatData.getSenderUid();
        String message = chatData.getMessage();

        String present_uid = getContext().getSharedPreferences("userInformation",
                Context.MODE_PRIVATE).getString("userUID","");

        if(uid.equals(present_uid)){
            // set the self message view and put message in selfMessage
            selfMessageView.setVisibility(View.VISIBLE);
            userMessageView.setVisibility(View.GONE);
            selfMessage.setText(message);

        }
        else{
            // set the user message view and put message in userMessage
            userMessageView.setVisibility(View.VISIBLE);
            selfMessageView.setVisibility(View.GONE);
            userMessage.setText(message);

        }

        return convertView;
    }
}
