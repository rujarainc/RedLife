package com.rujara.health.redlife.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by deep.patel on 2/23/16.
 */
public class OnBoardingAdapter extends FragmentPagerAdapter{
    private List<Fragment> fragments;
    public OnBoardingAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return this.fragments.get(position);
//        switch (position) {
//            case 0:
//                return OnBoardingFragment.newInstance(Color.parseColor("#03A9F4"), position); // blue
//            default:
//                return OnBoardingFragment.newInstance(Color.parseColor("#4CAF50"), position); // green
//        }
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
