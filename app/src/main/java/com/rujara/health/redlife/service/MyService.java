package com.rujara.health.redlife.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {

    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    ScheduledFuture worker = null;
    private boolean interrupted = false;
    private Runnable command = new Runnable() {

        @Override
        public void run() {
            if (interrupted)
                worker.cancel(true);
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            Log.d("[rujara]", String.valueOf(sessionManager.isLoggedIn()));
            if (!sessionManager.isLoggedIn())
                interruptThread();

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
                interruptThread();
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
                interruptThread();
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
        }
    };

    public MyService() {
        worker = service.scheduleAtFixedRate(command, 0, RedLifeContants.LOCATION_UPDATER_TIME, TimeUnit.MILLISECONDS);
    }

    private void interruptThread() {
        interrupted = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
