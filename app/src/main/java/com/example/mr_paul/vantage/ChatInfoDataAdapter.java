package com.example.mr_paul.vantage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatInfoDataAdapter extends ArrayAdapter<ChatInfoData> {

    public ChatInfoDataAdapter(Context context, int resources, ArrayList<ChatInfoData> list){
        super(context,resources,list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            // inflate a new view
            convertView = layoutInflater.inflate(R.layout.chat_name_layout,parent,false);
        }

        // find widgets references and put in values
        TextView charDisplay = convertView.findViewById(R.id.first_char_display);
        TextView userName = convertView.findViewById(R.id.user_name);

        ChatInfoData chatInfoData = getItem(position);
        charDisplay.setText(chatInfoData.getName().substring(0,1));
        userName.setText(chatInfoData.getName());

        return convertView;
    }
}
