package com.blackcatwalk.sharingpower;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

public class GroupMain extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ViewPagerAdapter mSectionsPagerAdapter;
    private Group_day group_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_group_main);

        mSectionsPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(4); // limit page lock 3page

        viewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getApplicationContext(), Group_create.class);
                startActivity(main);
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if (group_detail == null) {
                        group_detail = new Group_day();
                    }
                    return group_detail;
                case 1:
                    return new Group_hothit();
                case 2:
                    return new Group_follow();
                case 3:
                    return new Group_my();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ประจำวัน";
                case 1:
                    return "ฮอตฮิต";
                case 2:
                    return "ติดตาม";
                case 3:
                    return "กลุ่มที่สร้าง";
            }
            return null;
        }
    }
}
