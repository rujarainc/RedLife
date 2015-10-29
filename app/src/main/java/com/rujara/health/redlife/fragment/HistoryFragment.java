package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.activity.ResponseActivity;
import com.rujara.health.redlife.adapter.RowAdapterCardWithIcon;
import com.rujara.health.redlife.classes.CardObject;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by deep.patel on 9/18/15.
 */
public class HistoryFragment extends Fragment implements IAsyncTask {
    SessionManager sessionManager = null;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView noRecordsText;
    private ArrayList<CardObject> reqObject;
    private Communicator communicator = new Communicator(this);
    public HistoryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.history_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        noRecordsText = (TextView) rootView.findViewById(R.id.textView6);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterCardWithIcon(new ArrayList<CardObject>());

        mRecyclerView.setAdapter(mAdapter);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.history_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHistory();
            }
        });


        getHistory();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void onStart() {
        super.onStart();

    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPreExecute(int taskId) {
        if (taskId == 1) {
            if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
        }
    }

    @Override
    public void onPostExecute(int taskId, JSONObject response) {
        if (taskId == 1) {
            try {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                if (response.has("status") && response.getInt("status") == 0) {
                    noRecordsText.setVisibility(View.GONE);
                    JSONArray data = response.getJSONArray("data");
                    reqObject = new ArrayList<CardObject>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject temp = data.getJSONObject(i);
                        reqObject.add(new CardObject(temp.getString("id"), temp.getString("userAction"), new AppUtils().getDate(temp.getLong("actionTime"), "dd/MM/yyyy hh:mm:ss"), getResources().getDrawable(new AppUtils().getDrawableIconForBloodGroup(temp.getString(RedLifeContants.BLOOD_GROUP)))));
                    }
                    mAdapter = new RowAdapterCardWithIcon(reqObject);
                    ((RowAdapterCardWithIcon) mAdapter).setOnItemClickListener(new RowAdapterCardWithIcon.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {
                            Log.i("[rujara]", " Clicked on Item " + position);
                            Intent response = new Intent(getActivity(), ResponseActivity.class);
                            response.putExtra("requestId", reqObject.get(position).getId());
                            startActivity(response);
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                } else if (response.has("status") && response.getInt("status") == 5) {
                    reqObject = new ArrayList<CardObject>();
                    mAdapter = new RowAdapterCardWithIcon(reqObject);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    noRecordsText.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getHistory() {
        communicator.communicate(1, RedLifeContants.GET_MYACTION + "/" + sessionManager.getUserDetails().get("serverToken"));
    }
}
