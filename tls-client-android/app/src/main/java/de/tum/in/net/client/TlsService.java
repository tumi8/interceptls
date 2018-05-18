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

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 21.03.18.
 */

public class TlsService extends IntentService {

    private static final Logger log = LoggerFactory.getLogger(TlsService.class);

    public TlsService() {
        super("TlsService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_ID = "tls_service_channel";
            final NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "intercepTLS service",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            final Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_interception)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        log.debug("onHandleIntent TlsService");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            log.error("We do not have the permission to access the coarse location. Cannot execute test.");
            return;
        }

        TlsTestResult result = null;

        //ignore how much time since last scan passed
        final boolean force = intent.getBooleanExtra("force", false);

        final TlsDB db = new TlsDB(this);
        if (force || db.enoughTimeSinceLastScan()) {

            try {
                final List<HostAndPort> targets = ConfigurationReader.getTargets(this);
                final ClientWorkflowCallable c = new ClientWorkflowCallable(targets, new AndroidNetworkIdentifier(this));

                result = c.call();

            } catch (final Exception e) {
                log.error("Could not finish TLS test", e);
            }

        } else {
            log.info("Not enough time has passed since last scan, skip this round.");
        }

        if (result != null && result.anySuccessfulConnection()) {
            //save result
            db.saveResultTemp(result);

            //upload result
            final Intent i = new Intent(this, UploadResultService.class);
            this.startService(i);

            //inform user
            if (result.anyInterception()) {
                final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "")
                        .setSmallIcon(R.drawable.ic_interception)
                        .setContentTitle(this.getString(R.string.interception_title))
                        .setContentText("Your connection is intercepted!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(0, mBuilder.build());
            }

        }


        //publish result
        final ResultReceiver rec = intent.getParcelableExtra("resultReceiver");
        if (rec != null) {
            if (result != null && result.anySuccessfulConnection()) {
                final Bundle b = new Bundle();
                b.putString("timestamp", result.getTimestamp());
                rec.send(0, b);
            } else {
                rec.send(-1, null);
            }
        }

    }

}
