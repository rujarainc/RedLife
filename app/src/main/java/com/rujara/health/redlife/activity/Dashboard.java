package com.rujara.health.redlife.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import com.rujara.health.redlife.fragment.DashBoard_New;
import com.rujara.health.redlife.fragment.FragmentDrawer;
import com.rujara.health.redlife.fragment.HistoryFragment;
import com.rujara.health.redlife.fragment.HistoryHolder;
import com.rujara.health.redlife.fragment.ProfileFragment;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.networks.Signout;
import com.rujara.health.redlife.service.MyService;
import com.rujara.health.redlife.utils.SessionManager;

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
    private int currentVisibleFragment = -1;
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
//        displayView(0);
        if(RedLifeContants.OPEN_HISTORY){
            displayView(2);
            RedLifeContants.OPEN_HISTORY = false;
        } else {
            displayView(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // do your stuff  here
        if(currentVisibleFragment!=0)
            displayView(0);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            new Signout().execute(sessionManger.getUserDetails().get("serverToken"));
                            sessionManger.logoutUser();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Seriously.. You want to Logout?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
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
                fragment = new DashBoard_New();
                title = getString(R.string.title_dashboard);
                break;
            case 1:
                fragment = new ProfileFragment();
                title = getString(R.string.title_profile);
                break;
            case 2:
                fragment = new HistoryHolder();
                title = getString(R.string.title_medical_record);
                break;
            case 3:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, "Let's take a little step towards humanity. Step forward.\n"
//                        + "Presenting RedLife App for Blood seekers/Donors"
//                        + "https://play.google.com/store/apps/details?id=com.bt.bms"
//                        + "Download Now!!");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Text");
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                sendIntent.putExtra(Intent.EXTRA_STREAM, "https://play.google.com/store/apps/details?id=com.bt.bms");
//                sendIntent.putExtra(Intent.EXTRA_TEXT,""
//                        + "https://play.google.com/store/apps/details?id=com.bt.bms"
//                        + "Download Now!!");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            default:
                break;
        }

        currentVisibleFragment = position;
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

    public void onRequest(View view) {
        Intent donorList = new Intent(this, MapActivity.class);
        donorList.putExtra("makeRequest", true);
        startActivity(donorList);
    }

    public void onDonate(View view) {
        Intent donorList = new Intent(this, DonateActivity.class);
        startActivity(donorList);
    }
}
