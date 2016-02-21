package com.rujara.health.redlife.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.rujara.health.redlife.R;

public class SMSVerification extends AppCompatActivity {
    private EditText otpText;
    private ProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification);
        registerReceiver(broadcastReceiver, new IntentFilter("OTP_RECEIVED"));
        otpText = (EditText) findViewById(R.id.inputOtp);
        progressDialog = ProgressDialog.show(this, null, "Waiting for OTP ...", true);
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
}