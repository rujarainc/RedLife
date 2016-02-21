package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rujara.health.redlife.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryHolder.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryHolder#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class HistoryHolder extends Fragment {
    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_holder, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.historytabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.historyviewpager);
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyHistoryAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return rootView;
    }

    class MyHistoryAdapter extends FragmentPagerAdapter {

        public MyHistoryAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HistoryFragment();

                case 1:
                    return new ClosedRequest();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Open";
                case 1:
                    return "Closed";
            }
            return null;
        }
    }

}
