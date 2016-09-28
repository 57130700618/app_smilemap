package com.blackcatwalk.sharingpower;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.blackcatwalk.sharingpower.utility.Control;


public class MainActivity extends TabActivity {

    private TabHost mTabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        double _sizeInInches = new Control().getSizeScrren(this);

        int _size;

        if (_sizeInInches >= 9.5) {
            _size = 135;
        } else if (_sizeInInches >= 6.5) {
            _size = 95;
        } else {
            _size = 55;
        }

        mTabHost = getTabHost();

        TabHost.TabSpec _spec;

        _spec = mTabHost.newTabSpec("one").setIndicator("")
                .setContent(new Intent().setClass(this, LocationGps.class));
        mTabHost.addTab(_spec);

        _spec = mTabHost.newTabSpec("Two").setIndicator("")
                .setContent(new Intent().setClass(this, BusGps.class));
        mTabHost.addTab(_spec);

        _spec = mTabHost.newTabSpec("three").setIndicator("")
                .setContent(new Intent().setClass(this, FavoriteMain.class));
        mTabHost.addTab(_spec);

        _spec = mTabHost.newTabSpec("four").setIndicator("")
                .setContent(new Intent().setClass(this, Island.class));
        mTabHost.addTab(_spec);


        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu_location_white);
        mTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = (int) (_size * this.getResources().getDisplayMetrics().density);

        mTabHost.getTabWidget().getChildAt(1).getLayoutParams().height = (int) (_size * this.getResources().getDisplayMetrics().density);
        mTabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu_bus_blue);

        mTabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu_favorite_blue);
        mTabHost.getTabWidget().getChildAt(2).getLayoutParams().height = (int) (_size * this.getResources().getDisplayMetrics().density);


        mTabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu_island_blue);
        mTabHost.getTabWidget().getChildAt(3).getLayoutParams().height = (int) (_size * this.getResources().getDisplayMetrics().density);

        mTabHost.getTabWidget().setCurrentTab(0);

        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

                    if (i == 0) {
                        mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_location_blue);
                    } else if (i == 1) {
                        mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_bus_blue);
                    } else if (i == 2) {
                        mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_favorite_blue);
                    } else if (i == 3) {
                        mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_island_blue);
                    }
                }

                if (mTabHost.getCurrentTab() == 0)
                    mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_location_white);
                else if (mTabHost.getCurrentTab() == 1)
                    mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_bus_white);
                else if (mTabHost.getCurrentTab() == 2)
                    mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_favorite_white);
                else if (mTabHost.getCurrentTab() == 3) {
                    mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_island_white);
                }
            }
        });
    }

}

/*

        intent = new Intent().setClass(this, BusGps.class);
        spec = mTabHost.newTabSpec("BUS").setIndicator("")
                .setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, LocationGps.class);
        spec = mTabHost.newTabSpec("Two").setIndicator("")
                .setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, GroupMain.class);
        spec = mTabHost.newTabSpec("Three").setIndicator("")
                .setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, FavoriteMain.class);
        spec = mTabHost.newTabSpec("Four").setIndicator("")
                .setContent(intent);
        mTabHost.addTab(spec);

        intent = new Intent().setClass(this, Island.class);
        spec = mTabHost.newTabSpec("five").setIndicator("")
                .setContent(intent);
        mTabHost.addTab(spec);



     //picture default && set size Tabhost
        mTabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu_location_blue);
        mTabHost.getTabWidget().getChildAt(1).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        mTabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu_group_blue);
        mTabHost.getTabWidget().getChildAt(2).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        mTabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu_favorite_blue);
        mTabHost.getTabWidget().getChildAt(3).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        mTabHost.getTabWidget().getChildAt(4).setBackgroundResource(R.drawable.menu_island_blue);
        mTabHost.getTabWidget().getChildAt(4).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);

        mTabHost.getTabWidget().setCurrentTab(0);
        mTabHost.getTabWidget().getChildAt(0).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        mTabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu_bus_white);


        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

            mTabHost.setBackgroundResource(R.color.blueBold);
            if (i == 0) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_bus_blue);
            } else if (i == 1) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_location_blue);
            } else if (i == 2) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_group_blue);
            } else if (i == 3) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_favorite_blue);
            } else if (i == 4) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_island_blue);
            }
        }

        if (mTabHost.getCurrentTab() == 0)
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_bus_white);
        else if (mTabHost.getCurrentTab() == 1)
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_location_white);
        else if (mTabHost.getCurrentTab() == 2)
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_group_white);
        else if (mTabHost.getCurrentTab() == 3) {
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_favorite_white);
        } else if (mTabHost.getCurrentTab() == 4) {
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_island_white);
        }
 */