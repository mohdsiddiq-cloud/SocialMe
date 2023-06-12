package com.example.android.adapter;

import android.app.Notification;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.android.fragments.AddFragment;
import com.example.android.fragments.HomeFragment;
import com.example.android.fragments.NotificationFragment;
import com.example.android.fragments.ProfileFragment;
import com.example.android.fragments.SearchFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    int noOfTabs;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int noOfTabs) {
        super(fm);
        this.noOfTabs = noOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();

            case 1:
                return new SearchFragment();

            case 2:
                return new AddFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
