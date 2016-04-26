package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.networks.Signout;
import com.rujara.health.redlife.utils.AppUtils;
import com.rujara.health.redlife.utils.SessionManager;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RequestDetails extends AppCompatActivity implements LocationListener, INetworkListener {
    GoogleMap googleMap;
    double lat;
    double lon;
    private LatLng currentlatLng = null;
    private LatLng destinationlatlng = null;
    private Snackbar snackbar = null;
    private SessionManager sessionManger = null;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    private TextView inputName, detailsText;
//            inputEmail, inputPhone;
    private String requestId, fromEmail;
    private LatLng requestedLocation;
    private RelativeLayout detailsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        inputName = (TextView) findViewById(R.id.input_name);
//        inputEmail = (TextView) findViewById(R.id.input_email);
//        inputPhone = (TextView) findViewById(R.id.input_phone);
        detailsText = (TextView) findViewById(R.id.details);
        detailsLayout = (RelativeLayout) findViewById(R.id.detaislLayout);
        boolean fromNotification = getIntent().getBooleanExtra("fromNotification", false);
        String requestName = getIntent().getStringExtra(RedLifeContants.NAME);
        String requestEmail = getIntent().getStringExtra(RedLifeContants.EMAILID);
        String requestPhone = getIntent().getStringExtra(RedLifeContants.PHONE_NUMBER);
        String details = getIntent().getStringExtra(RedLifeContants.DETAILS);
        String respondTime = getIntent().getStringExtra(RedLifeContants.RESPOND_TIME);
        if(respondTime!=null && !respondTime.equalsIgnoreCase("") && !respondTime.equalsIgnoreCase("0")){
            TextView tvRespondTime = (TextView) findViewById(R.id.tvRespondTime);
            tvRespondTime.setText("Responded on " + new AppUtils().getDate(Long.parseLong(respondTime), "dd/MM/yyyy"));

            LinearLayout responseOptionsLayout = (LinearLayout) findViewById(R.id.responseOptionLayout);
            responseOptionsLayout.setVisibility(View.GONE);

            RelativeLayout respondTimeLayout = (RelativeLayout) findViewById(R.id.respondedTimeLayout);
            respondTimeLayout.setVisibility(View.VISIBLE);

        }
        requestId = getIntent().getStringExtra("requestId");
        inputName.setText(requestName);
//        inputEmail.setText(requestEmail);
//        inputPhone.setText(requestPhone);
        fromEmail = requestEmail;

        if(details!=null){
            detailsText.setText(details);
            detailsLayout.setVisibility(View.VISIBLE);
        }

        myView = findViewById(R.id.response_details_layout_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sessionManger = new SessionManager(getApplicationContext());
        sessionManger.checkLogin();
        networkInspector = new NetworkInspector(this, this);
        setSupportActionBar(toolbar);
        if (!fromNotification)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = supportMapFragment.getMap();
        requestedLocation = new LatLng(lat, lon);
        googleMap.setMyLocationEnabled(true);
        MarkerOptions marker = new MarkerOptions()
                .position(requestedLocation);
        googleMap.addMarker(marker);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Location location = getLastKnownLocation(locationManager);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (location != null) {
                    initMapLocation(location);
                }
            }
        });

        getSupportActionBar().setTitle("Request Details");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_response_details, menu);
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
            new Signout().execute(sessionManger.getUserDetails().get("serverToken"));
            sessionManger.logoutUser();
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void initMapLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        currentlatLng = new LatLng(latitude, longitude);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currentlatLng);
        builder.include(requestedLocation);
        LatLngBounds bounds = builder.build();
        int padding = 80; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);
    }

    @Override
    public void onLocationChanged(Location location) {
       /* double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        currentlatLng = new LatLng(latitude, longitude);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currentlatLng);
        builder.include(requestedLocation);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(currentlatLng)      // Sets the center of the map to Mountain View
//                .zoom(15)                   // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
//        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkInspector.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkInspector.start();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

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
                mLocationManager.requestLocationUpdates(provider, 2000, 0, this);
            }
        }

        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }

    public void onAccept(View view) {
        Intent greetings = new Intent(this, DonorGreetingsActivty.class);
        greetings.putExtra("requestId", requestId);
        greetings.putExtra("fromEmail", fromEmail);
        startActivity(greetings);
        finish();
    }

    public void onLater(View view) {
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}