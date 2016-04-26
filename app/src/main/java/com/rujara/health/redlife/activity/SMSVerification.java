package com.rujara.health.redlife.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.constants.RedLifeContants;
import com.rujara.health.redlife.networks.Communicator;
import com.rujara.health.redlife.networks.IAsyncTask;
import com.rujara.health.redlife.networks.INetworkListener;
import com.rujara.health.redlife.networks.NetworkInspector;
import com.rujara.health.redlife.store.UserDetails;
import com.rujara.health.redlife.utils.SessionManager;

import org.json.JSONObject;

public class SMSVerification extends AppCompatActivity implements INetworkListener, IAsyncTask {
    private EditText otpText;
    private ProgressDialog progressDialog = null;
    private Snackbar snackbar = null;
    private NetworkInspector networkInspector = null;
    private Communicator communicator = new Communicator(this);
    private View myView = null;
    private String smsCode;
    private SessionManager sessionManager;
    private TextView mobileNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = findViewById(R.id.smsverificationview);
        setContentView(R.layout.activity_smsverification);
        smsCode = getIntent().getStringExtra("smsCode");
        registerReceiver(broadcastReceiver, new IntentFilter("OTP_RECEIVED"));
        otpText = (EditText) findViewById(R.id.inputOtp);
        progressDialog = ProgressDialog.show(this, null, "Waiting for OTP ...", true, true);
        networkInspector = new NetworkInspector(this, this);
        sessionManager = new SessionManager(this);
        mobileNumber = (TextView) findViewById(R.id.txt_edit_mobile);
        mobileNumber.setText(getIntent().getStringExtra(RedLifeContants.PHONE_NUMBER));
        if(smsCode!=null){
            communicator.communicate(2, RedLifeContants.GET_VERIFY_CODE + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN) + "/" + getIntent().getStringExtra(RedLifeContants.PHONE_NUMBER));
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String otp = intent.getStringExtra("otp");
            if(otp!=null) {
                otpText.setText(otp);
                progressDialog.dismiss();
            }
        }
    };

    public void verify(View view){
        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        EditText editText = (EditText) findViewById(R.id.inputOtp);
        if(editText != null && smsCode != null && editText.getText().toString().equalsIgnoreCase(smsCode))
            communicator.communicate(1, RedLifeContants.VERIFY + "/" + sessionManager.getUserDetails().get(SessionManager.SERVER_TOKEN));
    }

    @Override
    public void onPreExecute(int taskId) {
    }

    @Override
    public void onPostExecute(int taskId, JSONObject jsonObject) {
        if(taskId==1){
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
        } else if(taskId==2){
            try{
                if(jsonObject.has("status") && jsonObject.getInt("status") == 0){
                    smsCode = jsonObject.getString("message");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
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
        unregisterReceiver(broadcastReceiver);
        finish();
    }
}