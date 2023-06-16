package com.example.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import com.example.android.adapter.ViewPagerAdapter;
import com.example.android.fragments.SearchFragment;
import com.example.android.socialme.R;
import com.google.android.material.tabs.TabLayout;

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
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person));

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
                     case 4:
                         tabLayout.getTabAt(4).setIcon(R.drawable.ic_person);
                         break;

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
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person);
                        break;

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
                    case 4:
                        tabLayout.getTabAt(4).setIcon(R.drawable.ic_person);
                        break;

                }
            }
        });
    }

    private void init() {


        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPage);

    }

    @Override
    public void change(int position) {
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==4){
            viewPager.setCurrentItem(0);
        }
        else
        super.onBackPressed();
    }
}