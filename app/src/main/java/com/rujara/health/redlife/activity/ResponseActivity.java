package com.rujara.health.redlife.activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.adapter.RowAdapterListWithIcon;
import com.rujara.health.redlife.classes.RequestObject;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ResponseActivity extends AppCompatActivity implements INetworkListener {
    //    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
    RowAdapterListWithIcon mAdapter;
    private ListView mListView;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    private SessionManager sessionManger = null;
    private Snackbar snackbar = null;
    private Toolbar mToolbar;
    private TextView waiting;
    private JSONObject data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response);
        boolean makeRequest = getIntent().getBooleanExtra("makeRequest", false);
        sessionManger = new SessionManager(getApplicationContext());
        sessionManger.checkLogin();
        if (makeRequest) {
            try {
                data = new JSONObject();
                UserDetails userDetails = UserDetails.getInstance();
                data.put(RedLifeContants.EMAILID, userDetails.getEmailId());
                data.put(RedLifeContants.BLOOD_GROUP, userDetails.getBloodGroup());
                data.put(RedLifeContants.NAME, userDetails.getName());
                data.put(RedLifeContants.PHONE_NUMBER, userDetails.getPhoneNumber());
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Log.d("ruajra", locationManager.getAllProviders().toString());
                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                Location currentLocation = null;
                double lat = 0;
                double lon = 0;
                String locationProvidedBy = null;
                if (!isGPSEnabled && !isNetworkEnabled) {
                } else if (isGPSEnabled) {
                    currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (currentLocation == null && isNetworkEnabled) {
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationProvidedBy = "network";
                    } else {
                        locationProvidedBy = "gps";
                    }
                }
                if (currentLocation == null) {

                } else {
                    lat = currentLocation.getLatitude();
                    lon = currentLocation.getLongitude();
                }
                if (lat == 0 && lon == 0 && locationProvidedBy != null) {
                    Location location = locationManager.getLastKnownLocation(locationProvidedBy);
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
                data.put("location", new JSONObject().put("lat", lat).put("lon", lon).put("providedBy", locationProvidedBy));
                new MakeRequest().execute(RedLifeContants.REQUEST + "/" + sessionManger.getUserDetails().get("serverToken"));
            } catch (Exception e) {
                Log.e("[rujara]", "Error creating request json", e);
            }

        }
        waiting = (TextView) findViewById(R.id.waitingDonorResponseText);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("Responses");
        networkInspector = new NetworkInspector(this, this);
        myView = findViewById(R.id.donor_response_layout);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mListView = (ListView) findViewById(R.id.donor_response_recycler_view);
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RowAdapterListWithIcon(this, getDataSet());

        mListView.setAdapter(mAdapter);

    }

    public void showList(View v) {
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
            System.out.println(
                    RedLifeContants.SIGNOUT + "/" + sessionManger.getUserDetails().get("serverToken")
            );
            new SignoutTask().execute(RedLifeContants.SIGNOUT + "/" + sessionManger.getUserDetails().get("serverToken"));
            sessionManger.logoutUser();
        }
        if (id == android.R.id.home) {
            RedLifeContants.OPEN_HISTORY = true;
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<RequestObject> getDataSet() {
        ArrayList results = new ArrayList<RequestObject>();
        for (int index = 0; index < 20; index++) {
            RequestObject obj = new RequestObject(null, "Some Primary Text " + index,
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

    private class SignoutTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            UserDetails.getInstance().resetUser();
        }
    }

    private class MakeRequest extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(args[0]);
                String json = "";
                json = data.toString();
                Log.v("[rujara]", "Req: " + json);
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
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

        }
    }
}
