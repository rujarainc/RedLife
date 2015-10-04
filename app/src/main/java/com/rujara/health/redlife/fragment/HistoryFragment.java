package com.rujara.health.redlife.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.activity.ResponseActivity;
import com.rujara.health.redlife.adapter.RowAdapterCardWithIcon;
import com.rujara.health.redlife.classes.RequestObject;
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

/**
 * Created by deep.patel on 9/18/15.
 */
public class HistoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<RequestObject> reqObject;
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
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterCardWithIcon(getDataSet());
//        System.out.println(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        final SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.history_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new EndpointCommunicationTask().execute(RedLifeContants.GET_MYACTION + "/" + sessionManager.getUserDetails().get("serverToken"));
            }
        });
        ((RowAdapterCardWithIcon) mAdapter).setOnItemClickListener(new RowAdapterCardWithIcon.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i("[rujara]", " Clicked on Item " + position);
                Intent response = new Intent(getActivity(), ResponseActivity.class);
                response.putExtra("requestId", reqObject.get(position).getId());
                startActivity(response);
            }
        });

        new EndpointCommunicationTask().execute(RedLifeContants.GET_MYACTION + "/" + sessionManager.getUserDetails().get("serverToken"));
        // Inflate the layout for this fragment
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

    private ArrayList<RequestObject> getDataSet() {
        ArrayList results = new ArrayList<RequestObject>();
//        for (int index = 0; index < 20; index++) {
//            RequestObject obj = new RequestObject(null, "Some Primary Text " + index,
//                    "Secondary " + index, getResources().getDrawable(R.drawable.a_plus));
//            results.add(index, obj);
//        }
        return results;
    }

    private class EndpointCommunicationTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressDialog = ProgressDialog.show(getActivity(), null, "Fetching actions ...", true);
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
                if (response.has("status") && response.getInt("status") == 0) {
                    JSONArray data = response.getJSONArray("data");
                    reqObject = new ArrayList<RequestObject>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject temp = data.getJSONObject(i);
                        reqObject.add(new RequestObject(temp.getString("id"), temp.getString("userAction"), new AppUtils().getDate(temp.getLong("actionTime"), "dd/MM/yyyy hh:mm:ss"), getResources().getDrawable(new AppUtils().getDrawableIconForBloodGroup(temp.getString(RedLifeContants.BLOOD_GROUP)))));
                    }
                    mAdapter = new RowAdapterCardWithIcon(reqObject);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            } catch (Exception e) {

            }

        }
    }
}
