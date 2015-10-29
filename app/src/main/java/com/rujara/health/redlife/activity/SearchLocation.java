package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.adapter.RowAdapterListWithIconPlace;
import com.rujara.health.redlife.classes.CardObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchLocation extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "PlaceAutocompleteAdptr";
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    private static final CharacterStyle STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
    private static final LatLngBounds INDIA = new LatLngBounds(
            //new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
            //67.016667	6.755556	97.35	35.9558333333333
            new LatLng(6.755556, 67.016667), new LatLng(35.955833, 97.35));
    protected GoogleApiClient mGoogleApiClient;
    ArrayList<CardObject> cardObjects = new ArrayList<CardObject>();
    private RowAdapterListWithIconPlace mAdapter;
    private EditText searchText;
    private ListView searchResults;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        searchText = (EditText) findViewById(R.id.addressText);
        searchResults = (ListView) findViewById(R.id.search_location_list_view);
        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getPlaceByPlaceId(cardObjects.get(i).getId(), i);
            }
        });
        mAdapter = new RowAdapterListWithIconPlace(this, cardObjects);
        searchResults.setAdapter(mAdapter);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                if (cs != null && cs.length() > 0) {
                    new GetLocationAsync().execute(cs.toString());
                } else {
                    mAdapter = new RowAdapterListWithIconPlace(SearchLocation.this, new ArrayList<CardObject>());
                    searchResults.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {

        return null;
    }

    private void getPlaceByPlaceId(String placeId, final int position) {
        if (mGoogleApiClient.isConnected()) {
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                if (places != null) {
                                    final Place myPlace = places.get(0);
                                    latLng = myPlace.getLatLng();
                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    returnIntent.putExtra("lat", latLng.latitude);
                                    returnIntent.putExtra("lon", latLng.longitude);
                                    places.release(); //If something goes wrong remove this out after two curly braces from current position
                                    finish();
                                }
                            }
                        }
                    });
        } else {
            Log.e(TAG, "Google API client is not connected for autocomplete query.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.stay, R.anim.slide_out_down);
    }

    private class GetLocationAsync extends AsyncTask<String, Void, ArrayList<AutocompletePrediction>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ArrayList<AutocompletePrediction> doInBackground(String... params) {
            if (mGoogleApiClient.isConnected()) {
                Log.i(TAG, "Starting autocomplete query for: " + params[0]);

                // Submit the query to the autocomplete API and retrieve a PendingResult that will
                // contain the results when the query completes.
                List<Integer> filterTypes = new ArrayList<Integer>();
//                filterTypes.add( Place.TYPE_AIRPORT );
                filterTypes.add(Place.TYPE_ESTABLISHMENT);

                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi
                                .getAutocompletePredictions(mGoogleApiClient, params[0],
                                        INDIA, null);

                // This method should have been called off the main UI thread. Block and wait for at most 60s
                // for a result from the API.
                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);

                // Confirm that the query completed successfully, otherwise return null
                final com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {

                    Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                    autocompletePredictions.release();
                    return null;
                }

                Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                        + " predictions.");

                // Freeze the results immutable representation that can be stored safely.
                return DataBufferUtils.freezeAndClose(autocompletePredictions);
            }
            Log.e(TAG, "Google API client is not connected for autocomplete query.");

            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<AutocompletePrediction> results) {
            cardObjects = new ArrayList<CardObject>();
            mAdapter = new RowAdapterListWithIconPlace(SearchLocation.this, cardObjects);
            searchResults.setAdapter(mAdapter);
            for (AutocompletePrediction prediction : results) {
                cardObjects.add(new CardObject(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_BOLD).toString(), prediction.getSecondaryText(STYLE_NORMAL).toString(), "", null));
            }
            mAdapter.notifyDataSetChanged();
        }
    }
}
