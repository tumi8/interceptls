package de.tum.in.net.client;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by johannes on 26.03.18.
 */

public class TlsJobService extends JobService {

    private static final Logger log = LoggerFactory.getLogger(TlsJobService.class);

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        log.debug("onStartJob TLS Job Service");

        final Intent i = new Intent(this, TlsService.class);
        startService(i);

        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters jobParameters) {
        return true;
    }


}
