package com.rujara.health.redlife.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

import com.rujara.health.redlife.constants.RedLifeContants;
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

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class LocationUpdater extends IntentService {

    public LocationUpdater() {
        super("LocationUpdater");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        while (true) {
            if (!sessionManager.isLoggedIn())
                break;

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
                break;
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
                break;
            } else {
                lat = currentLocation.getLatitude();
                lon = currentLocation.getLongitude();
            }
            JSONObject response = null;
            try {
                InputStream inputStream = null;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(RedLifeContants.NOTIFY_LOCATION + "/" + sessionManager.getUserDetails().get("serverToken"));
                String json = "{\"lat\":\"" + lat + "\",\"lon\":\"" + lon + "\",\"locationProvider\":\"" + locationProvidedBy + "\"}";
                Log.d("[rujara]", "Req: " + json);
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

            try {
                Thread.sleep(RedLifeContants.LOCATION_UPDATER_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class LocationNotifierTask extends AsyncTask<String, Void, JSONObject> {
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
        }
    }
}
