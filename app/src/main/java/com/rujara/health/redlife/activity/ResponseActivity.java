package com.rujara.health.redlife.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.adapter.RowAdapterListWithIcon;
import com.rujara.health.redlife.classes.CardObject;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.networks.Signout;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResponseActivity extends AppCompatActivity implements INetworkListener, IAsyncTask {
    //    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
    private Communicator communicator = new Communicator(this);
    private RowAdapterListWithIcon mAdapter;
    private ListView mListView;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    private SessionManager sessionManger = null;
    private Snackbar snackbar = null;
    private Toolbar mToolbar;
    private TextView waiting;
    private JSONObject data;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String locationProvidedBy = null;
    private String requestId, locationString, details, bloodGroup;
    private double lat;
    private double lon;
    private ArrayList<CardObject> resObject;
    private TextView noRecordsText;
    private boolean makeRequest;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        noRecordsText = (TextView) findViewById(R.id.noRecordsFound);
        makeRequest = getIntent().getBooleanExtra("makeRequest", false);
        sessionManger = new SessionManager(getApplicationContext());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        sessionManger.checkLogin();
        if (makeRequest) {
            lat = getIntent().getDoubleExtra("lat", 0);
            lon = getIntent().getDoubleExtra("lon", 0);
            details = getIntent().getStringExtra("details");
            bloodGroup = getIntent().getStringExtra("bloodGroup");
            locationString = getIntent().getStringExtra("locationString");
            makeRequest();
        } else {
            requestId = getIntent().getStringExtra("requestId");
            getResponse(RedLifeContants.GET_RESPONSES + "/" + sessionManger.getUserDetails().get(SessionManager.SERVER_TOKEN) + "/" + requestId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResponse(RedLifeContants.GET_RESPONSES + "/" + sessionManger.getUserDetails().get(SessionManager.SERVER_TOKEN) + "/" + requestId);
            }
        });
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Responses");
        networkInspector = new NetworkInspector(this, this);
        myView = findViewById(R.id.donor_response_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListView = (ListView) findViewById(R.id.donor_response_list_view);
        mAdapter = new RowAdapterListWithIcon(this, new ArrayList<CardObject>());
        mListView.setAdapter(mAdapter);
    }

    public void showList(View v) {
        swipeRefreshLayout.setRefreshing(false);
        waiting.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_response, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            new Signout().execute(sessionManger.getUserDetails().get("serverToken"));
            sessionManger.logoutUser();
        }
        if (id == android.R.id.home) {
            RedLifeContants.OPEN_HISTORY = true;
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<CardObject> getDataSet() {
        ArrayList results = new ArrayList<CardObject>();
        for (int index = 0; index < 20; index++) {
            CardObject obj = new CardObject(null, "Some Primary Text " + index,
                    "Secondary " + index, getResources().getDrawable(R.drawable.ic_profile));
            results.add(index, obj);
        }
        return results;
    }

    @Override
    public void onNetworkConnected() {
        if (snackbar != null)
            snackbar.dismiss();
    }

    @Override
    public void onNetWorkConnectionFail() {
        if (snackbar == null) {
            snackbar = Snackbar.make(myView, "Data Connection Lost..", Snackbar.LENGTH_INDEFINITE);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(getResources().getColor(R.color.windowBackground));
        }
        snackbar.show();
    }

    protected void onResume() {
        super.onResume();
        networkInspector.start();
    }

    protected void onPause() {
        super.onPause();
        networkInspector.stop();
    }

    private void makeRequest() {
        try {
            data = new JSONObject();
            UserDetails userDetails = UserDetails.getInstance();
            if (bloodGroup == null)
                data.put(RedLifeContants.BLOOD_GROUP, userDetails.getBloodGroup());
            else
                data.put(RedLifeContants.BLOOD_GROUP, bloodGroup);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (lat == 0 && lon == 0) {
                Location location = getLastKnownLocation(locationManager);
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
            }
            data.put("location", new JSONObject().put("lat", lat).put("lon", lon).put("providedBy", locationProvidedBy).put("locationString", locationString));
            data.put("details", details);
            communicator.communicate(1, RedLifeContants.REQUEST + "/" + sessionManger.getUserDetails().get("serverToken"), data);
//            new MakeRequest().execute(RedLifeContants.REQUEST + "/" + sessionManger.getUserDetails().get("serverToken"));
        } catch (Exception e) {
            Log.e("[rujara]", "Error creating request json", e);
        }
    }

    private void getResponse(String url) {
        communicator.communicate(2, url);
    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
                locationProvidedBy = provider;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    @Override
    public void onPreExecute(int taskId) {
        if (taskId == 1) {
            progressDialog = ProgressDialog.show(ResponseActivity.this, null, "Posting Request ...", true);
        } else if (taskId == 2) {
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
            progressDialog.dismiss();
        } else if (taskId == 2) {
            swipeRefreshLayout.setRefreshing(false);
            try {
                if (response.has("status") && response.getInt("status") == 0) {
                    noRecordsText.setVisibility(View.GONE);
                    JSONArray data = response.getJSONArray("data");
                    resObject = new ArrayList<CardObject>();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject temp = data.getJSONObject(i);
                        CardObject cardObject = new CardObject(temp.getString("id"), temp.getString(RedLifeContants.NAME), temp.getString(RedLifeContants.EMAILID), temp.getString("phoneNo"), null);
                        cardObject.addToData(RedLifeContants.NAME, temp.getString(RedLifeContants.NAME));
                        cardObject.addToData(RedLifeContants.EMAILID, temp.getString(RedLifeContants.EMAILID));
                        cardObject.addToData(RedLifeContants.PHONE_NUMBER, temp.getString("phoneNo"));
                        cardObject.addToData("lon", String.valueOf(temp.getDouble("longitude")));
                        cardObject.addToData("lat", String.valueOf(temp.getDouble("latitude")));
                        resObject.add(cardObject);
                    }
                    mAdapter = new RowAdapterListWithIcon(ResponseActivity.this, resObject);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            CardObject cardObject = resObject.get(i);
                            Intent requestDetail = new Intent(ResponseActivity.this, ResponseDetails.class);
                            requestDetail.putExtra("lat", Double.valueOf(cardObject.getFromData("lat")));
                            requestDetail.putExtra("lon", Double.valueOf(cardObject.getFromData("lon")));
                            requestDetail.putExtra(RedLifeContants.NAME, cardObject.getFromData(RedLifeContants.NAME));
                            requestDetail.putExtra(RedLifeContants.EMAILID, cardObject.getFromData(RedLifeContants.EMAILID));
                            requestDetail.putExtra(RedLifeContants.PHONE_NUMBER, cardObject.getFromData(RedLifeContants.PHONE_NUMBER));
                            requestDetail.putExtra("responseId", cardObject.getId());
                            requestDetail.putExtra("requestId", requestId);
                            startActivity(requestDetail);
                        }
                    });
                    mAdapter.notifyDataSetChanged();

                } else if (response.has("status") && response.getInt("status") == 5) {
                    resObject = new ArrayList<CardObject>();
                    mAdapter = new RowAdapterListWithIcon(ResponseActivity.this, resObject);
                    mListView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    noRecordsText.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("[rujara]", e.getLocalizedMessage(), e);
                resObject = new ArrayList<CardObject>();
                mAdapter = new RowAdapterListWithIcon(ResponseActivity.this, resObject);
                mListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                noRecordsText.setVisibility(View.VISIBLE);
            }
        }
    }
}
