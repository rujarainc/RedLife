package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.fragment.DashboardFragment;
import com.rujara.health.redlife.fragment.FragmentDrawer;
import com.rujara.health.redlife.fragment.MedicalRecordFragment;
import com.rujara.health.redlife.fragment.ProfileFragment;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.service.MyService;
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
public class Dashboard extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, INetworkListener {

    private static String TAG = Dashboard.class.getSimpleName();
    GoogleCloudMessaging gcm = null;
    private Snackbar snackbar = null;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    private SessionManager sessionManger = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
//        new RegisterGcmTask().execute();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        myView = findViewById(R.id.drawer_layout);

        sessionManger = new SessionManager(getApplicationContext());
        sessionManger.checkLogin();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Log.v("RUJARA", "value of ActionBar: " + getActionBar());
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        networkInspector = new NetworkInspector(this, this);
        Intent i = new Intent(Dashboard.this, MyService.class);
        startService(i);
        displayView(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new DashboardFragment();
                title = getString(R.string.title_dashboard);
                break;
            case 1:
                fragment = new ProfileFragment();
                title = getString(R.string.title_profile);
                break;
            case 2:
                fragment = new MedicalRecordFragment();
                title = getString(R.string.title_medical_record);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            getSupportActionBar().setTitle(title);
        }
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

    public void onSearchDonors(View view) {
        Intent donorList = new Intent(this, ResponseActivity.class);
        donorList.putExtra("makeRequest", true);
        startActivity(donorList);
    }

    public void onDonate(View view) {
        Intent donorList = new Intent(this, DonateActivity.class);
        startActivity(donorList);
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
}
