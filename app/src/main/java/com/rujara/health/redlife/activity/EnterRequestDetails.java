package com.rujara.health.redlife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.utils.SessionManager;

public class EnterRequestDetails extends AppCompatActivity implements INetworkListener {

    private SessionManager sessionManger = null;
    private NetworkInspector networkInspector = null;
    private View myView = null;
    private Snackbar snackbar = null;
    private String selectedBloodGroup = null;
    private EditText details;
    private TextView remainingChars;
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            remainingChars.setText(String.valueOf(160 - s.length()) + " characters remaining");
        }
    };
    private double lat;
    private double lon;
    private String locationString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_request_details);

        lat = getIntent().getDoubleExtra("lat", 0);
        lon = getIntent().getDoubleExtra("lon", 0);
        locationString = getIntent().getStringExtra("locationString");

        remainingChars = (TextView) findViewById(R.id.remainingChars);
        details = (EditText) findViewById(R.id.input_details);
        details.addTextChangedListener(mTextEditorWatcher);
        details.clearFocus();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Enter Request Details");
        sessionManger = new SessionManager(getApplicationContext());
        sessionManger.checkLogin();
        myView = findViewById(R.id.enterRequestView);
        networkInspector = new NetworkInspector(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_donate, menu);
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
        }
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        networkInspector.start();
    }

    protected void onPause() {
        super.onPause();
        networkInspector.stop();
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

    public void onProceed(View view) {
        Intent responseList = new Intent(this, ResponseActivity.class);
        responseList.putExtra("lat", lat);
        responseList.putExtra("lon", lon);
        responseList.putExtra("locationString", locationString);
        responseList.putExtra("details", details.getText().toString());
        responseList.putExtra("makeRequest", true);
        responseList.putExtra("bloodGroup", selectedBloodGroup);
        startActivity(responseList);
    }

    public void onBack(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    public void onBGSelect(View view) {
        int id = view.getId();
        deSelectBloodGroupResourceId();
        switch (id) {
            case R.id.ap:
                selectedBloodGroup = "a+ve";
                ((ImageView) findViewById(R.id.ap)).setImageDrawable(getResources().getDrawable(R.drawable.a_plus_primary));
                break;

            case R.id.am:
                selectedBloodGroup = "a-ve";
                ((ImageView) findViewById(R.id.am)).setImageDrawable(getResources().getDrawable(R.drawable.a_minus_primary));
                break;
            case R.id.bp:
                selectedBloodGroup = "b+ve";
                ((ImageView) findViewById(R.id.bp)).setImageDrawable(getResources().getDrawable(R.drawable.b_plus_primary));
                break;
            case R.id.bm:
                selectedBloodGroup = "b-ve";
                ((ImageView) findViewById(R.id.bm)).setImageDrawable(getResources().getDrawable(R.drawable.b_minus_primary));
                break;
            case R.id.abp:
                selectedBloodGroup = "ab+ve";
                ((ImageView) findViewById(R.id.abp)).setImageDrawable(getResources().getDrawable(R.drawable.ab_plus_primary));
                break;
            case R.id.abm:
                selectedBloodGroup = "ab-ve";
                ((ImageView) findViewById(R.id.abm)).setImageDrawable(getResources().getDrawable(R.drawable.ab_minus_primary));
                break;
            case R.id.op:
                selectedBloodGroup = "o+ve";
                ((ImageView) findViewById(R.id.op)).setImageDrawable(getResources().getDrawable(R.drawable.o_plus_primary));
                break;
            case R.id.om:
                selectedBloodGroup = "o-ve";
                ((ImageView) findViewById(R.id.om)).setImageDrawable(getResources().getDrawable(R.drawable.o_minus_primary));
                break;
        }
        if (selectedBloodGroup != null) {
            ((Button) findViewById(R.id.proceedButton)).setEnabled(true);
            details.setEnabled(true);
        }
    }

    private void deSelectBloodGroupResourceId() {
        int resourceId = 0;
        if (selectedBloodGroup == null) {

        } else if (selectedBloodGroup.equalsIgnoreCase("a+ve")) {
            ((ImageView) findViewById(R.id.ap)).setImageDrawable(getResources().getDrawable(R.drawable.a_plus));
        } else if (selectedBloodGroup.equalsIgnoreCase("a-ve")) {
            ((ImageView) findViewById(R.id.am)).setImageDrawable(getResources().getDrawable(R.drawable.a_minus));
        } else if (selectedBloodGroup.equalsIgnoreCase("b+ve")) {
            ((ImageView) findViewById(R.id.bp)).setImageDrawable(getResources().getDrawable(R.drawable.b_plus));
        } else if (selectedBloodGroup.equalsIgnoreCase("b-ve")) {
            ((ImageView) findViewById(R.id.bm)).setImageDrawable(getResources().getDrawable(R.drawable.b_minus));
        } else if (selectedBloodGroup.equalsIgnoreCase("ab+ve")) {
            ((ImageView) findViewById(R.id.abp)).setImageDrawable(getResources().getDrawable(R.drawable.ab_plus));
        } else if (selectedBloodGroup.equalsIgnoreCase("ab-ve")) {
            ((ImageView) findViewById(R.id.abm)).setImageDrawable(getResources().getDrawable(R.drawable.ab_minus));
        } else if (selectedBloodGroup.equalsIgnoreCase("o+ve")) {
            ((ImageView) findViewById(R.id.op)).setImageDrawable(getResources().getDrawable(R.drawable.o_plus));
        } else if (selectedBloodGroup.equalsIgnoreCase("o-ve")) {
            ((ImageView) findViewById(R.id.om)).setImageDrawable(getResources().getDrawable(R.drawable.o_minus));
        }
    }

}
