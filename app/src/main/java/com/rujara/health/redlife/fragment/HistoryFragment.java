package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.activity.Dashboard;
import com.rujara.health.redlife.activity.ResponseActivity;
import com.rujara.health.redlife.activity.SearchLocation;
import com.rujara.health.redlife.activity.XPRating;
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
    public void onPostExecute(int taskId, final JSONObject response) {
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
                        String locationString = "Unknown Location";
                        String responseReceived = "No Response Received";
                        if(temp.has("locationString") && temp.getString("locationString")!=null && !temp.getString("locationString").equalsIgnoreCase("null") && !temp.getString("locationString").equalsIgnoreCase(""))
                            locationString = temp.getString("locationString");
                        if(temp.has("responseCount") && temp.getInt("responseCount")==1){
                            responseReceived = temp.getInt("responseCount") + " Response received.";
                        } else if(temp.has("responseCount") && temp.getInt("responseCount")>1){
                            responseReceived = temp.getInt("responseCount") + " Responses received.";
                        }
                        CardObject cardObject = new CardObject(temp.getString("id"), locationString, responseReceived, new AppUtils().getDate(temp.getLong("actionTime"), "dd/MM/yyyy HH:mm:ss"), getResources().getDrawable(new AppUtils().getDrawableIconForBloodGroup(temp.getString(RedLifeContants.BLOOD_GROUP))));
//                        if(temp.has("complete") && temp.getBoolean("complete"))
//                            cardObject.setStatusIcon(getResources().getDrawable(R.drawable.ic_action_assignment_turned_in));
//                        else
//                            cardObject.setStatusIcon(getResources().getDrawable(R.drawable.ic_action_assignment_late));
                        reqObject.add(cardObject);

                    }
                    mAdapter = new RowAdapterCardWithIcon(reqObject, true);
                    ((RowAdapterCardWithIcon) mAdapter).setOnItemClickListener(new RowAdapterCardWithIcon.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {


                            Intent response = new Intent(getActivity(), ResponseActivity.class);
                            response.putExtra("requestId", reqObject.get(position).getId());
                            startActivity(response);
                        }
                    });
                    ((RowAdapterCardWithIcon) mAdapter).setOnMenuItemClickListener(new RowAdapterCardWithIcon.MyMenuItenClickListener() {
                        @Override
                        public boolean onItemClick(MenuItem menuItem, View v, int position) {
                            int menuindex = menuItem.getItemId();
                            switch (menuindex) {
                                case 0:
//                                    Intent rate = new Intent(getActivity(), XPRating.class);
//                                    rate.putExtra("requestId", reqObject.get(position).getId());
//                                    startActivity(rate);
                                    Intent rate = new Intent(getActivity(), XPRating.class);
                                    rate.putExtra(RedLifeContants.REQUEST_ID, reqObject.get(position).getId());
                                    getActivity().startActivityForResult(rate, RedLifeContants.ActivityResults.ON_REQUEST_CLOSED);
                                    break;
                                case 1:
                                    System.out.println(reqObject.get(position).getId() +  ": 1");
                                    break;
                                default:
                                    System.out.println("-1");
                                    break;
                            }
                            return false;
                        }
                    });

                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                } else if (response.has("status") && response.getInt("status") == 5) {
                    reqObject = new ArrayList<CardObject>();
                    mAdapter = new RowAdapterCardWithIcon(reqObject, true);
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
        communicator.communicate(1, RedLifeContants.GET_MYACTION + "/" + sessionManager.getUserDetails().get("serverToken") + "/" + 1);
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
        mRecyclerView.setHasFixedSize(false);
        noRecordsText = (TextView) rootView.findViewById(R.id.textView6);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterCardWithIcon(new ArrayList<CardObject>(), true);
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
        registerForContextMenu(mRecyclerView);
        getHistory();
        return rootView;
    }

}
