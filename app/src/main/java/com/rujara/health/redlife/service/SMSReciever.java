package com.rujara.health.redlife.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.rujara.health.redlife.activity.SMSVerification;

/**
 * Created by deep.patel on 12/14/15.
 */
public class SMSReciever extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();
    public void onReceive(Context context, Intent intent) {
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);
                    // Show Alert
                    int duration = Toast.LENGTH_LONG;
//                    Toast toast = Toast.makeText(context,
//                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    if(senderNum!=null && (senderNum.contains("+912222") || senderNum.contains("redlif"))){
                        Intent sendOTP = new Intent("OTP_RECEIVED");
                        sendOTP.putExtra("otp", fetchOTP(message));
                        context.sendBroadcast(sendOTP);
                    }

//                    toast.show();
                } // end for loop
            } // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }

    private String fetchOTP(String message){
        if(message!=null){
            String otp = message.substring(17,21);
            return otp;
        }
        return null;
    }
}
