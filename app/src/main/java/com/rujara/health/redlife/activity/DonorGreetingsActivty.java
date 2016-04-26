package com.rujara.health.redlife.activity;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONObject;

import java.util.List;

public class DonorGreetingsActivty extends AppCompatActivity implements IAsyncTask {

    private JSONObject data = new JSONObject();
    private String locationProvider = null;
    private Communicator communicator = new Communicator(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_greetings_activty);
        String requestId = getIntent().getStringExtra("requestId");
        String fromEmail = getIntent().getStringExtra("fromEmail");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("You did it");
        SessionManager sessionManager = new SessionManager(this);
        UserDetails userDetails = UserDetails.getInstance();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location currentLocation = getLastKnownLocation(locationManager);
        try {
            JSONObject location = new JSONObject();
            location.put("lat", currentLocation.getLatitude());
            location.put("lon", currentLocation.getLongitude());
            location.put("providedBy", locationProvider);
            data.put(RedLifeContants.NAME, userDetails.getName());
            data.put(RedLifeContants.EMAILID, userDetails.getEmailId());
            data.put(RedLifeContants.PHONE_NUMBER, userDetails.getPhoneNumber());
            data.put("fromEmail", fromEmail);
            data.put("location", location);
        } catch (Exception e) {

        }
        submitResponse(RedLifeContants.RESPOND + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN) + "/" + requestId, data);
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

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                locationProvider = provider;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    private void submitResponse(String url, JSONObject data) {
        communicator.communicate(1, url, data);
    }

    @Override
    public void onPreExecute(int taskId) {
    }

    @Override
    public void onPostExecute(int taskId, JSONObject jsonObject) {

    }
}
