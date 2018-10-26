package com.example.mr_paul.vantage;


import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppIntroductionActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private IntroPagerAdapter introPagerAdapter;
    TextView[] mDots;
    private LinearLayout mDotLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_introduction);

        // to make the screen go full
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        viewPager = findViewById(R.id.intro_view_pager);
        mDotLayout = findViewById(R.id.mDotsLayout);

        introPagerAdapter = new IntroPagerAdapter(AppIntroductionActivity.this);
        viewPager.setAdapter(introPagerAdapter);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageSelected(int i) {
                addDotsIndicator(i);
            }
            @Override
            public void onPageScrollStateChanged(int i) {}
        });

    }

    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for(int i=0; i<mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparentWhite));

            mDotLayout.addView(mDots[i]);

        }

        mDots[position].setTextColor(getResources().getColor(R.color.darkerWhite));

    }
}
