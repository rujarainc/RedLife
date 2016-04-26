package com.rujara.health.redlife.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static String ShopLat;
    public static String ShopPlaceId;
    public static String ShopLong;
    boolean mUpdatesRequested = false;
    private LocationRequest mLocationRequest;
    private GoogleMap mGoogleMap;
    private int GET_LOCATION = 1;
    // Stores the current instantiation of the location client in this object
    private GoogleApiClient mGoogleApiClient;
    private LatLng center;
    private LinearLayout markerLayout;
    private Geocoder geocoder;
    private List<Address> addresses;
    private TextView Address;
    private Button proceed, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        Address = (TextView) findViewById(R.id.addressText);
        markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        proceed = (Button) findViewById(R.id.proceedButton);
        cancel = (Button) findViewById(R.id.cancelButton);
        proceed.getBackground().setAlpha(150);
        cancel.getBackground().setAlpha(150);
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                    requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment
            // Create a new global location parameters object
            mLocationRequest = LocationRequest.create();

            /*
             * Set the update interval
             */
            mLocationRequest.setInterval(RedLifeContants.UPDATE_INTERVAL_IN_MILLISECONDS);

            // Use high accuracy
            mLocationRequest
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Set the interval ceiling to one minute
            mLocationRequest
                    .setFastestInterval(RedLifeContants.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

            // Note that location updates are off until the user turns them on
            mUpdatesRequested = false;
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            mGoogleApiClient.connect();
        }

    }


    private void stupMap() {
        try {
            LatLng latLong;
            // TODO Auto-generated method stub
            mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            View mapView = (FrameLayout) findViewById(R.id.map);
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            // and next place it, for exemple, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 180);

            // Enabling MyLocation in Google Map
            mGoogleMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location location = getLastKnownLocation(locationManager);
            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(13).build();

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            // Clears all the existing markers
            mGoogleMap.clear();

            mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition arg0) {
                    // TODO Auto-generated method stub
                    center = mGoogleMap.getCameraPosition().target;

                    mGoogleMap.clear();
                    markerLayout.setVisibility(View.VISIBLE);

                    try {
                        new GetLocationAsync(center.latitude, center.longitude)
                                .execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            markerLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    try {

                        LatLng latLng1 = new LatLng(center.latitude,
                                center.longitude);

                        Marker m = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng1)
                                .title(" Set your Location ")
                                .snippet("")
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_maps_place)));
                        m.setDraggable(true);

                        markerLayout.setVisibility(View.GONE);
                    } catch (Exception e) {
                    }

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openSearch(View view) {
        Intent openSearch = new Intent(this, SearchLocation.class);
        startActivityForResult(openSearch, GET_LOCATION);
        // overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                double latitude = data.getDoubleExtra("lat", 0);
                double longitude = data.getDoubleExtra("lon", 0);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latitude, longitude)).zoom(13).build();
                mGoogleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
//    protected void onResume(){
//        super.onPause();
//        overridePendingTransition(R.anim.slide_out_down, R.anim.slide_in_down);
//    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        stupMap();

    }

    @Override
    public void onConnectionSuspended(int i) {

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

    public void onCancel(View view) {
        NavUtils.navigateUpFromSameTask(this);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onProceed(View view) {
        Intent proceedActivity = new Intent(this, EnterRequestDetails.class);
        proceedActivity.putExtra("lat", mGoogleMap.getCameraPosition().target.latitude);
        proceedActivity.putExtra("lon", mGoogleMap.getCameraPosition().target.longitude);
        proceedActivity.putExtra("locationString", Address.getText().toString());
        startActivity(proceedActivity);
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {
            // TODO Auto-generated constructor stub
            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
            Address.setText("Fetching Address");
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                geocoder = new Geocoder(MapActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();
                if (geocoder.isPresent()) {
                    if (addresses.size() == 0)
                        return null;
                    Address returnAddress = addresses.get(0);
                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();
                    str.append(localityString + "");
                    str.append(city + "" + region_code + "");
                    str.append(zipcode + "");

                } else {
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Address.setText(addresses.get(0).getAddressLine(0)
                        + ", " + addresses.get(0).getAddressLine(1) + " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
