package com.example.mr_paul.vantage;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IntroPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;


    public IntroPagerAdapter(Context context){
        this.context = context;
    }

    // background assets
    int background[] = {
            R.drawable.intro_bg,
            R.drawable.background,
            R.drawable.bg
    };

    // image assets
    int images[] = {
            R.drawable.launcher_icon,
            R.drawable.logo,
            R.drawable.coding
    };

    // headers
    public String[] slideHeading = {
            "Welcome",
            "Why use this app?",
            "Eat.Sleep.Code"
    };

    // descriptions
    public String[] descriptions = {
            "Welcome to Vantage. This is an customer eccentric app for buying or selling products amongst students of our institute.",
            "\"Be Freed from Budget!\" - that's it. As students are not financially independent, to keep up with their ever growing wish list, an online portal of buying/selling would be much beneficiary!",
            "Eat.Sleep.Code and let us handle your worries about Budget. Register yourself to be a member of Vantage and get a good vantage point of life! "
    };



    @Override
    public int getCount() {
        return slideHeading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.introduction_layout,container,false);

        // reference to the views and set values
        LinearLayout linearLayout = view.findViewById(R.id.linear_layout);
        ImageView imageView = view.findViewById(R.id.image_view);
        TextView HeadingTextView = view.findViewById(R.id.heading);
        TextView DescriptionTextView = view.findViewById(R.id.description);
        Button proceed = view.findViewById(R.id.proceed);

        linearLayout.setBackgroundResource(background[position]);
        imageView.setImageResource(images[position]);
        HeadingTextView.setText(slideHeading[position]);
        DescriptionTextView.setText(descriptions[position]);

        if(position == slideHeading.length-1){

            DescriptionTextView.setBackgroundColor(context.getResources().getColor(R.color.transparentBlack));

            proceed.setVisibility(View.VISIBLE);
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // write to the shared preference and call an intent to login activity
                    context.getSharedPreferences("userInformation",Context.MODE_PRIVATE)
                            .edit().putBoolean("showAppIntro",false).apply();
                    context.startActivity(new Intent(context,LoginActivity.class));

                }
            });
        }
        else{
            proceed.setVisibility(View.GONE);
        }


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
