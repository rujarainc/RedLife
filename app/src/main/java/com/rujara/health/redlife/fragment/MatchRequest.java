package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.adapter.RowAdapterCardWithIcon;
import com.rujara.health.redlife.classes.RequestObject;

import java.util.ArrayList;

public class MatchRequest extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public MatchRequest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_match_request, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.matched_request_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterCardWithIcon(getDataSet());
        System.out.println(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        ((RowAdapterCardWithIcon) mAdapter).setOnItemClickListener(new RowAdapterCardWithIcon.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i("[rujara]", " Clicked on Item " + position);
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private ArrayList<RequestObject> getDataSet() {
        ArrayList results = new ArrayList<RequestObject>();
        for (int index = 0; index < 20; index++) {
            RequestObject obj = new RequestObject("Some Primary Text " + index,
                    "Secondary " + index, getResources().getDrawable(R.drawable.a_plus));
            results.add(index, obj);
        }
        return results;
    }
}
