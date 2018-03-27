package de.tum.in.net.client;

import android.app.job.JobParameters;
import android.app.job.JobService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 26.03.18.
 */

public class TlsJobService extends JobService {

    private static final Logger log = LoggerFactory.getLogger(TlsJobService.class);
    private TlsTestTask task;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        log.error("call start job tls service");

        task = new TlsTestTask(this, new AsyncResult<TlsTestResult>() {
            @Override
            public void publishResult(final TlsTestResult result) {
                //depending on the result we could reschedule the job
                // e.g. reschedule if result == null || !result.anySuccessfulConnection()
                jobFinished(jobParameters, false);
                task = null;
            }
        });

        task.execute();

        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters jobParameters) {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        return true;
    }


}
