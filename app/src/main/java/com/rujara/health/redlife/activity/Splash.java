package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.utils.SessionManager;

/**
 * Created by deep.patel on 9/18/15.
 */
public class Splash extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 1000;
    private SessionManager sessionManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent landingActivity = null;
                if (sessionManager.isLoggedIn()) {
                    landingActivity = new Intent(Splash.this, Dashboard.class);
                } else {
                    landingActivity = new Intent(Splash.this, LoginActivity.class);
                }
                startActivity(landingActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);
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
}
