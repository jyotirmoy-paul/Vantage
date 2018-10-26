package com.example.mr_paul.vantage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PageAdapter extends FragmentStatePagerAdapter{

    private int numberOfTabs;

    // default constructor
    public PageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numberOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            // switch to the tab as necessary
            case 0:
                NewsFeed newsFeed = new NewsFeed();
                return newsFeed;
            case 1:
                PostAd postAd = new PostAd();
                return postAd;
            case 2:
                Profile profile = new Profile();
                return profile;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
