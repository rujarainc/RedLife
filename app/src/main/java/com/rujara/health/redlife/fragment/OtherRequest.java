package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class OtherRequest extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayout;
    private TextView noRecordsText;
    private ArrayList<CardObject> reqObject;

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
        final SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        noRecordsText = (TextView) rootView.findViewById(R.id.noRecordsFound);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.other_request_swipelayout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new EndpointCommunicationTask().execute(RedLifeContants.OTHER_REQUEST + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN));
            }
        });
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.other_request_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterCardWithIcon(new ArrayList<CardObject>());
        mRecyclerView.setAdapter(mAdapter);

        new EndpointCommunicationTask().execute(RedLifeContants.OTHER_REQUEST + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN));
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


    private class EndpointCommunicationTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (swipeRefreshLayout != null && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(args[0]);
                httpGet.setHeader("Accept", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpGet);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null)
                    response = new AppUtils().convertInputStreamToJson(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            Log.v("[rujara]", "Response: " + response);
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
                        reqObject.add(cardObject);
                    }
                    mAdapter = new RowAdapterCardWithIcon(reqObject);
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
                            requestDetail.putExtra("requestId", cardObject.getId());

                            startActivity(requestDetail);
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
//
//    mRecyclerView.setOnScrollListener(new ScrollListenerImpl(getActivity().getApplicationContext()) {
//        @Override
//        public void onMoved(int distance) {
//            android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
//            TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
//            toolbar.setTranslationY(-distance);
//            tabLayout.setTranslationY(-distance);
//        }
//    });
}
