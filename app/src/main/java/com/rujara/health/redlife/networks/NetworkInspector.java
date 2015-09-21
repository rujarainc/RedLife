package com.rujara.health.redlife.networks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rujara.health.redlife.constants.RedLifeContants;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by deep.patel on 9/21/15.
 */
public class NetworkInspector {
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    Runnable command = null;
    private ScheduledFuture<?> scheduleAtFixedRate = null;

    public NetworkInspector(final Context context, final INetworkListener listener) {
        command = new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connMgr = (ConnectivityManager)
                        context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    listener.onNetworkConnected();
                } else {
                    listener.onNetWorkConnectionFail();
                }
            }
        };
    }

    public void start() {
        scheduleAtFixedRate = scheduler.scheduleAtFixedRate(command, 0, RedLifeContants.NETWORK_INSPECTOR_THREAD_SLEEP_TIME, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduleAtFixedRate.cancel(true);
    }
}
