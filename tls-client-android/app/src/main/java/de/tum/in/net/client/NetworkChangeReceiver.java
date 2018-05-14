package de.tum.in.net.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final Logger log = LoggerFactory.getLogger(NetworkChangeReceiver.class);

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        log.error("onReceive");

        // a new connection may need some time to be established
        sleepSilently(4000);

        if (isOnline(ctx)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //from oreo on startService throws an IllegalStateException, see https://developer.android.com/about/versions/oreo/android-8.0-changes
                ctx.startForegroundService(new Intent(ctx, TlsService.class));
            } else {
                ctx.startService(new Intent(ctx, TlsService.class));
            }
        }

        log.error("onReceive done");
    }

    private void sleepSilently(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            //ignore
        }
    }


    public boolean isOnline(final Context ctx) {
        final ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}
