package com.blackcatwalk.sharingpower;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;


public class MainActivity extends TabActivity {

    private TabHost tabHost;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        double sizeInInches = Control.getSizeScrren(this);

        int size;

        if (sizeInInches >= 9.5) {
            size = 135;
        } else if (sizeInInches >= 6.5) {
            size = 95;
        } else {
            size = 55;
        }

        tabHost = getTabHost();

        TabHost.TabSpec spec;

     /*   intent = new Intent().setClass(this, BusGps.class);
        spec = tabHost.newTabSpec("BUS").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, LocationGps.class);
        spec = tabHost.newTabSpec("Two").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, GroupMain.class);
        spec = tabHost.newTabSpec("Three").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FavoriteMain.class);
        spec = tabHost.newTabSpec("Four").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Island.class);
        spec = tabHost.newTabSpec("five").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);
*/

        intent = new Intent().setClass(this, LocationGps.class);
        spec = tabHost.newTabSpec("one").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, BusGps.class);
        spec = tabHost.newTabSpec("Two").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FavoriteMain.class);
        spec = tabHost.newTabSpec("three").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Island.class);
        spec = tabHost.newTabSpec("four").setIndicator("")
                .setContent(intent);
        tabHost.addTab(spec);

     /*   //picture default && set size Tabhost
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu_location_blue);
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu_group_blue);
        tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        tabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu_favorite_blue);
        tabHost.getTabWidget().getChildAt(3).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        tabHost.getTabWidget().getChildAt(4).setBackgroundResource(R.drawable.menu_island_blue);
        tabHost.getTabWidget().getChildAt(4).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);

        tabHost.getTabWidget().setCurrentTab(0);
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = (int) (60 * this.getResources().getDisplayMetrics().density);
        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu_bus_white);*/


        tabHost.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.menu_location_white);
        tabHost.getTabWidget().getChildAt(0).getLayoutParams().height = (int) (size * this.getResources().getDisplayMetrics().density);

        tabHost.getTabWidget().getChildAt(1).getLayoutParams().height = (int) (size * this.getResources().getDisplayMetrics().density);
        tabHost.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.menu_bus_blue);

        tabHost.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.menu_favorite_blue);
        tabHost.getTabWidget().getChildAt(2).getLayoutParams().height = (int) (size * this.getResources().getDisplayMetrics().density);


        tabHost.getTabWidget().getChildAt(3).setBackgroundResource(R.drawable.menu_island_blue);
        tabHost.getTabWidget().getChildAt(3).getLayoutParams().height = (int) (size * this.getResources().getDisplayMetrics().density);

        tabHost.getTabWidget().setCurrentTab(0);

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
    /*  for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

            tabHost.setBackgroundResource(R.color.blueBold);
            if (i == 0) {
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_bus_blue);
            } else if (i == 1) {
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_location_blue);
            } else if (i == 2) {
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_group_blue);
            } else if (i == 3) {
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_favorite_blue);
            } else if (i == 4) {
                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_island_blue);
            }
        }

        if (tabHost.getCurrentTab() == 0)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_bus_white);
        else if (tabHost.getCurrentTab() == 1)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_location_white);
        else if (tabHost.getCurrentTab() == 2)
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_group_white);
        else if (tabHost.getCurrentTab() == 3) {
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_favorite_white);
        } else if (tabHost.getCurrentTab() == 4) {
            tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_island_white);
        }*/

                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {

                  //  tabHost.setBackgroundResource(R.color.blueBold);
                    if (i == 0) {
                        tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_location_blue);
                    } else if (i == 1) {
                        tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_bus_blue);
                    } else if (i == 2) {
                        tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_favorite_blue);
                    } else if (i == 3) {
                        tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.menu_island_blue);
                    }
                }

                if (tabHost.getCurrentTab() == 0)
                    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_location_white);
                else if (tabHost.getCurrentTab() == 1)
                    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_bus_white);
                else if (tabHost.getCurrentTab() == 2)
                    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_favorite_white);
                else if (tabHost.getCurrentTab() == 3) {
                    tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundResource(R.drawable.menu_island_white);
                }
            }
        });
    }

}

