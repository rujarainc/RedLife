package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;


/**
 * Created by deep.patel on 9/18/15.
 */
public class Splash extends AppCompatActivity implements IAsyncTask {

    private final int SPLASH_TIME_OUT = 1000;
    private SessionManager sessionManager = null;
    private Intent landingActivity = null;
    private UserDetails userDetails = null;
    private Communicator communicator = new Communicator(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Crashlytics.getInstance().core.setUserName("");
        Crashlytics.getInstance().core.setUserEmail("");
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());
        userDetails = UserDetails.getInstance();
        Log.d("[rujara]", "Logged In" + sessionManager.isLoggedIn());
        if (!sessionManager.isLoggedIn()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    landingActivity = new Intent(Splash.this, OnboardingActivity.class);
                    startActivity(landingActivity);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
//            new GetUserTask().execute(RedLifeContants.GET_USER + "/" + sessionManager.getUserDetails().get("serverToken"));
            getUser(RedLifeContants.GET_USER + "/" + sessionManager.getUserDetails().get("serverToken"));
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

    public void getUser(String url) {
        communicator.communicate(1, url);
    }

    @Override
    public void onPreExecute(int taskId) {
    }

    @Override
    public void onPostExecute(int taskId, JSONObject response) {
        if (taskId == 1) {
            String smsVerificationCode = null;
            try {
                if (response.has("status")) {
                    if (response.getInt("status") == 0) {
                        JSONObject data = response.getJSONObject("data");
                        smsVerificationCode = data.getString(RedLifeContants.SMS_VERIFICATION_CODE);
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
                        Crashlytics.getInstance().core.setUserName(userDetails.getName());
                        Crashlytics.getInstance().core.setUserEmail(userDetails.getEmailId());

                        if(smsVerificationCode!=null && !smsVerificationCode.equalsIgnoreCase("null")){
                            landingActivity = new Intent(Splash.this, SMSVerification.class);
                            landingActivity.putExtra(RedLifeContants.PHONE_NUMBER, userDetails.getPhoneNumber());
                            landingActivity.putExtra("smsCode", smsVerificationCode);
                            startActivity(landingActivity);
                        }else{
                            landingActivity = new Intent(Splash.this, Dashboard.class);
                            startActivity(landingActivity);
                        }

                        finish();
                    } else {
                        landingActivity = new Intent(Splash.this, OnboardingActivity.class);
                        startActivity(landingActivity);
                        finish();
                    }
                }
            } catch (Exception e) {
                Log.e("[rujara]", "Redirecting to Login page. Reason[Exception]", e);
                landingActivity = new Intent(Splash.this, OnboardingActivity.class);
                startActivity(landingActivity);
                finish();
            }
        }
    }

//    private class GetUserTask extends AsyncTask<String, Void, JSONObject> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//        @Override
//        protected JSONObject doInBackground(String... args) {
//            return new Communicator(args[0]).communicate();
//        }
//        @Override
//        protected void onPostExecute(JSONObject response) {
//            Log.v("[rujara]", "Response: " + response);
//            try {
//                if (response.has("status")) {
//                    if (response.getInt("status") == 0) {
//                        JSONObject data = response.getJSONObject("data");
//                        if (data.has(RedLifeContants.NAME))
//                            userDetails.setName(data.getString(RedLifeContants.NAME));
//                        if (data.has(RedLifeContants.EMAILID))
//                            userDetails.setEmailId(data.getString(RedLifeContants.EMAILID));
//                        if (data.has(RedLifeContants.PHONE_NUMBER))
//                            userDetails.setPhoneNumber(data.getString(RedLifeContants.PHONE_NUMBER));
//                        if (data.has(RedLifeContants.DOB))
//                            userDetails.setDob(data.getString(RedLifeContants.DOB));
//                        if (data.has(RedLifeContants.BLOOD_GROUP))
//                            userDetails.setBloodGroup(data.getString(RedLifeContants.BLOOD_GROUP));
//                        if (data.has(RedLifeContants.SERVER_AUTH_TOKEN))
//                            userDetails.setServerAuthToken(data.getString(RedLifeContants.SERVER_AUTH_TOKEN));
//                        Crashlytics.getInstance().core.setUserName(userDetails.getName());
//                        Crashlytics.getInstance().core.setUserEmail(userDetails.getEmailId());
//                        landingActivity = new Intent(Splash.this, Dashboard.class);
//                        startActivity(landingActivity);
//                        finish();
//                    } else {
//                        landingActivity = new Intent(Splash.this, LoginActivity.class);
//                        startActivity(landingActivity);
//                        finish();
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("[rujara]", "Redirecting to Login page. Reason[Exception]", e);
//                landingActivity = new Intent(Splash.this, LoginActivity.class);
//                startActivity(landingActivity);
//                finish();
//            }
//        }
//    }
}