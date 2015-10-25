package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by deep.patel on 9/18/15.
 */
public class Splash extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 1000;
    private SessionManager sessionManager = null;
    private Intent landingActivity = null;
    private UserDetails userDetails = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = UserDetails.getInstance();
        Log.d("[rujara]", "Logged In" + sessionManager.isLoggedIn());
        if (!sessionManager.isLoggedIn()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    landingActivity = new Intent(Splash.this, LoginActivity.class);
                    startActivity(landingActivity);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            new GetUserTask().execute(RedLifeContants.GET_USER + "/" + sessionManager.getUserDetails().get("serverToken"));
        }
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent landingActivity = null;
//                if (sessionManager.isLoggedIn()) {
//                    landingActivity = new Intent(Splash.this, Dashboard.class);
//                } else {
//                    landingActivity = new Intent(Splash.this, LoginActivity.class);
//                }
//                startActivity(landingActivity);
//                finish();
//            }
//        }, SPLASH_TIME_OUT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class GetUserTask extends AsyncTask<String, Void, JSONObject> {
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
            try {
                if (response.has("status")) {
                    if (response.getInt("status") == 0) {
                        JSONObject data = response.getJSONObject("data");
                        if (data.has(RedLifeContants.NAME))
                            userDetails.setName(data.getString(RedLifeContants.NAME));
                        if (data.has(RedLifeContants.EMAILID))
                            userDetails.setEmailId(data.getString(RedLifeContants.EMAILID));
                        if (data.has(RedLifeContants.PHONE_NUMBER))
                            userDetails.setPhoneNumber(data.getString(RedLifeContants.PHONE_NUMBER));
                        if (data.has(RedLifeContants.DOB))
                            userDetails.setDob(data.getString(RedLifeContants.DOB));
                        if (data.has(RedLifeContants.BLOOD_GROUP))
                            userDetails.setBloodGroup(data.getString(RedLifeContants.BLOOD_GROUP));
                        if (data.has(RedLifeContants.SERVER_AUTH_TOKEN))
                            userDetails.setServerAuthToken(data.getString(RedLifeContants.SERVER_AUTH_TOKEN));
                        landingActivity = new Intent(Splash.this, Dashboard.class);
                        startActivity(landingActivity);
                        finish();
                    } else {
                        landingActivity = new Intent(Splash.this, LoginActivity.class);
                        startActivity(landingActivity);
                        finish();
                    }
                }
            } catch (Exception e) {
                Log.e("[rujara]", "Redirecting to Login page. Reason[Exception]", e);
                landingActivity = new Intent(Splash.this, LoginActivity.class);
                startActivity(landingActivity);
                finish();
            }
        }
    }
}
