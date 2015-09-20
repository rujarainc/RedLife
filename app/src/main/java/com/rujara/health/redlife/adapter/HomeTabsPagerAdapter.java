package com.rujara.health.redlife.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rujara.health.redlife.fragment.HistoryFragment;
import com.rujara.health.redlife.fragment.HomeFragment;

/**
 * Created by deep.patel on 9/18/15.
 */
public class HomeTabsPagerAdapter extends FragmentPagerAdapter {

    public HomeTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new HomeFragment();
            case 1:
                // Games fragment activity
                return new HistoryFragment();
        }

        return null;
    }

    /*@Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Home";
            case 1:
                return "History";

        }
        return null;
    }*/
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
}
