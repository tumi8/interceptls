/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private static final Logger log = LoggerFactory.getLogger(NetworkChangeReceiver.class);

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        log.error("onReceive");

        final PendingResult pendingResult = goAsync();
        final Intent i = new Intent(ctx, TlsService.class);
        i.putExtra("resultReceiver", new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(final int resultCode, final Bundle resultData) {
                pendingResult.finish();
            }
        });

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                // a new connection may need some time to be established
                sleepSilently(4000);


                if (isOnline(ctx)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //from oreo on startService throws an IllegalStateException, see https://developer.android.com/about/versions/oreo/android-8.0-changes
                        ctx.startForegroundService(i);
                    } else {
                        ctx.startService(i);
                    }
                } else {
                    pendingResult.finish();
                }
            }
        };
        final Thread t = new Thread(r);
        t.start();
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
