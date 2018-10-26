package com.example.mr_paul.vantage;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;

public class ServerDataAdapter extends ArrayAdapter<ServerData> {

    int resource;

    // the default constructor of the class
    public ServerDataAdapter(Context context, int resource, ArrayList<ServerData> arrayList){
        super(context,resource,arrayList);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // layout inflater
        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            // inflate a new view
            convertView = layoutInflater.inflate(resource,parent,false);
        }

        // referencing to the views
        ImageView adCoverImage = convertView.findViewById(R.id.ad_cover_image);
        TextView adChoice = convertView.findViewById(R.id.choice);
        TextView adPrice = convertView.findViewById(R.id.price);
        TextView adTitle = convertView.findViewById(R.id.ad_title);
        TextView userName = convertView.findViewById(R.id.user_name);


        ServerData serverData = getItem(position);

        // load data in the layout

        // loading the cover photo
        Uri coverPhoto = Uri.parse(serverData.getPhoto_01());
        Glide.with(getContext()).load(coverPhoto).into(adCoverImage);

        // formatting and setting the price
        String price = serverData.getPrice();
        if(price.length() != 0){
            // then ad for sale
            DecimalFormat decimalFormat = new DecimalFormat("##,##,##,###");
            price = decimalFormat.format(Integer.parseInt(price));
            price = "\u20B9 " + price;
            adPrice.setText(price);
        }
        else{
            adPrice.setText("-----");
        }

        // displaying the ad choice
        if(serverData.getChoice().equals("Ad for Sale")){
            adChoice.setText("For Sale");
        }
        else{
            adChoice.setText("For Rent");
        }

        // displaying ad title
        adTitle.setText(serverData.getAdTitleText());

        // displaying username
        if(getContext().getSharedPreferences("userInformation",Context.MODE_PRIVATE)
                .getString("userUID","").equals(serverData.getUserUID())){
            userName.setText("Me");
        }
        else{
            userName.setText(serverData.getUserName());
        }

        return convertView;
    }

}
