package com.rujara.health.redlife.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.fragment.DashboardFragment;
import com.rujara.health.redlife.fragment.MedicalRecordFragment;
import com.rujara.health.redlife.fragment.ProfileFragment;

import java.io.IOException;

/**
 * Created by deep.patel on 9/18/15.
 */
public class Dashboard extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener{

    private static String TAG = Dashboard.class.getSimpleName();
    GoogleCloudMessaging gcm = null;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        new RegisterGcmTask().execute();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Log.v("RUJARA", "value of ActionBar: " + getActionBar());
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

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

    class RegisterGcmTask extends AsyncTask<Void, Void, String> {
        String msg = "";

        @Override
        protected String doInBackground(Void... voids) {
            try {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                msg = gcm.register("398373931836");
//                gcm.unregister();
                RedLifeContants.GCM = gcm;
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Log.v("[rujara]", "Response: " + msg);

//            Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
//            Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
//            serviceIntent.putExtra("regId", msg);
//            startActivity(intent);
//            startService(serviceIntent);
        }
    }
}
