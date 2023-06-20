package com.example.android;

import static com.example.android.utils.Constants.PREF_DIRECTORY;
import static com.example.android.utils.Constants.PREF_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.example.android.adapter.ViewPagerAdapter;
import com.example.android.fragments.SearchFragment;
import com.example.android.socialme.R;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements SearchFragment.OnDataPass {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    ViewPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        addTabs();
    }

    private void addTabs() {
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_search));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_add));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_favorite_border_24));

        SharedPreferences sharedPreferences= getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String directory = sharedPreferences.getString(PREF_DIRECTORY,"");
        Bitmap bitmap= loadProfileImage(directory);
        Drawable drawable= new BitmapDrawable(getResources(),bitmap);

        tabLayout.addTab(tabLayout.newTab().setIcon(drawable));

        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        pagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                 viewPager.setCurrentItem(tab.getPosition());

                 switch (tab.getPosition()){
                     case 0:
                         tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
                         break;
                     case 1:
                         tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
                         break;
                     case 2:
                         tabLayout.getTabAt(2).setIcon(R.drawable.ic_add);
                         break;
                     case 3:
                         tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_favorite_24);
                         break;
//                     case 4:
//                         tabLayout.getTabAt(4).setIcon(R.drawable.ic_person);
//                         break;

                 }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_favorite_border_24);
                        break;
//                    case 4:
//                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person);
//                        break;

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
                        break;
                    case 1:
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_search);
                        break;
                    case 2:
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_add);
                        break;
                    case 3:
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_favorite_24);
                        break;
//                    case 4:
//                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person);
//                        break;

                }
            }
        });
    }

    private Bitmap loadProfileImage(String directory){
        try {
            File file= new File(directory, "profile.png");
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void init() {


        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPage);
    }



    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==4){
            viewPager.setCurrentItem(0);
            isSearchUser=false;
        }
        else
        super.onBackPressed();
    }
    public static String User_ID;
    public static boolean isSearchUser= false;

    @Override
    public void change(String uid) {
        User_ID= uid;
        isSearchUser=true;
        viewPager.setCurrentItem(4);
    }
}