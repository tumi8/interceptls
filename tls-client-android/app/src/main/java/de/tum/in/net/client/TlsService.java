package de.tum.in.net.client;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

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
    protected void onHandleIntent(@Nullable final Intent intent) {
        log.debug("onHandleIntent TlsService");

        TlsTestResult result = null;

        final TlsDB db = new TlsDB(this);
        if (db.enoughTimeSinceLastScan()) {

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
