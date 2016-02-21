package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.activity.RequestDetails;
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

public class OtherRequest extends Fragment implements IAsyncTask {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayout;
    private TextView noRecordsText;
    private ArrayList<CardObject> reqObject;
    private Communicator communicator = new Communicator(this);
    private SessionManager sessionManager;
    public OtherRequest() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_other_request, container, false);
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        noRecordsText = (TextView) rootView.findViewById(R.id.noRecordsFound);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.other_request_swipelayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOtherRequest();
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.other_request_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterCardWithIcon(new ArrayList<CardObject>(), false);
        mRecyclerView.setAdapter(mAdapter);

        getOtherRequest();
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

    private ArrayList<CardObject> getDataSet() {
        ArrayList results = new ArrayList<CardObject>();
        for (int index = 0; index < 20; index++) {
            CardObject obj = new CardObject(null, "Some Primary Text " + index,
                    "Secondary " + index, getResources().getDrawable(R.drawable.a_plus));
            results.add(index, obj);
        }
        return results;
    }

    private void getOtherRequest() {
        communicator.communicate(1, RedLifeContants.OTHER_REQUEST + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN));
    }

    @Override
    public void onPreExecute(int taskId) {
        if (taskId == 1) {
            if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
        }
    }

    @Override
    public void onPostExecute(int taskId, JSONObject response) {
        if (taskId == 1) {
            try {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (response.has("status") && response.getInt("status") == 0) {
                    noRecordsText.setVisibility(View.GONE);
                    JSONArray data = response.getJSONArray("data");
                    reqObject = new ArrayList<CardObject>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject temp = data.getJSONObject(i);
                        CardObject cardObject = new CardObject(temp.getString("id"), temp.getString(RedLifeContants.NAME), temp.getString(RedLifeContants.EMAILID), new AppUtils().getDate(temp.getLong("timestamp"), "dd/MM/yyyy HH:mm:ss"), getResources().getDrawable(new AppUtils().getDrawableIconForBloodGroup(temp.getString(RedLifeContants.BLOOD_GROUP))));
                        cardObject.addToData(RedLifeContants.NAME, temp.getString(RedLifeContants.NAME));
                        cardObject.addToData(RedLifeContants.EMAILID, temp.getString(RedLifeContants.EMAILID));
                        cardObject.addToData(RedLifeContants.PHONE_NUMBER, temp.getString("phoneNo"));
                        cardObject.addToData("lon", String.valueOf(temp.getDouble("longitude")));
                        cardObject.addToData("lat", String.valueOf(temp.getDouble("latitude")));
                        if(temp.has(RedLifeContants.DETAILS) && temp.getString(RedLifeContants.DETAILS)!=null && temp.getString(RedLifeContants.DETAILS).equalsIgnoreCase("null") && temp.getString(RedLifeContants.DETAILS).equalsIgnoreCase(""))
                            cardObject.addToData(RedLifeContants.DETAILS, temp.getString(RedLifeContants.DETAILS));
                        reqObject.add(cardObject);
                    }
                    mAdapter = new RowAdapterCardWithIcon(reqObject, false);
                    ((RowAdapterCardWithIcon) mAdapter).setOnItemClickListener(new RowAdapterCardWithIcon.MyClickListener() {
                        @Override
                        public void onItemClick(int position, View v) {

                            CardObject cardObject = reqObject.get(position);
                            Intent requestDetail = new Intent(getActivity(), RequestDetails.class);
                            requestDetail.putExtra("lat", Double.valueOf(cardObject.getFromData("lat")));
                            requestDetail.putExtra("lon", Double.valueOf(cardObject.getFromData("lon")));
                            requestDetail.putExtra(RedLifeContants.NAME, cardObject.getFromData(RedLifeContants.NAME));
                            requestDetail.putExtra(RedLifeContants.EMAILID, cardObject.getFromData(RedLifeContants.EMAILID));
                            requestDetail.putExtra(RedLifeContants.PHONE_NUMBER, cardObject.getFromData(RedLifeContants.PHONE_NUMBER));
                            requestDetail.putExtra(RedLifeContants.DETAILS, cardObject.getFromData(RedLifeContants.DETAILS));
                            requestDetail.putExtra("requestId", cardObject.getId());

                            startActivity(requestDetail);
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                } else if (response.has("status") && response.getInt("status") == 5) {
                    reqObject = new ArrayList<CardObject>();
                    mAdapter = new RowAdapterCardWithIcon(reqObject, false);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    noRecordsText.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
