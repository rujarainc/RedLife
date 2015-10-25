package com.rujara.health.redlife.listeners;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.rujara.health.redlife.R;
import com.rujara.health.redlife.activity.RequestDetails;
import com.rujara.health.redlife.constants.RedLifeContants;

import org.json.JSONObject;

/**
 * Created by deep.patel on 9/20/15.
 */
public class GcmListenerServiceImpl extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(message);
        // [END_EXCLUDE]
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {

        Intent intent = new Intent(this, RequestDetails.class);
        String gcmMessage = null;

        try {
            JSONObject jsonObject = new JSONObject(message);
            gcmMessage = jsonObject.getString("gcmMessage");
            intent.putExtra("fromNotification", true);
            intent.putExtra("lat", jsonObject.getDouble("lat"));
            intent.putExtra("lon", jsonObject.getDouble("lon"));
            intent.putExtra(RedLifeContants.NAME, jsonObject.getString(RedLifeContants.NAME));
            intent.putExtra(RedLifeContants.EMAILID, jsonObject.getString(RedLifeContants.EMAILID));
            intent.putExtra(RedLifeContants.PHONE_NUMBER, jsonObject.getString(RedLifeContants.PHONE_NUMBER));
            intent.putExtra("requestId", jsonObject.getString("requestId"));

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } catch (Exception e) {
            e.printStackTrace();
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(gcmMessage)
                .setContentText("Click for Details")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
