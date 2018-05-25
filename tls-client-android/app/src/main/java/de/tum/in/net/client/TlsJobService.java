/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.client;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by johannes on 26.03.18.
 */

public class TlsJobService extends JobService {

    private static final Logger log = LoggerFactory.getLogger(TlsJobService.class);

    public static void init(final Context ctx) {
        final JobScheduler js = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        final int timeInMinutes = ConfigurationReader.readServiceTime(ctx);

        if (timeInMinutes == 0) {
            js.cancel(JobId.tlsJobServiceId);
        } else {
            final long timeInMillis = timeInMinutes * 60 * 1000;
            log.info("Set TlsJobService service time in min: {}", timeInMinutes);

            final JobInfo job = new JobInfo.Builder(
                    JobId.tlsJobServiceId,
                    new ComponentName(ctx, TlsJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPeriodic(timeInMillis)
                    //.setBackoffCriteria(5 * 60 * 1000, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                    .build();
            js.schedule(job);
        }
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        log.debug("onStartJob TLS Job Service");

        final Intent i = new Intent(this, TlsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //from oreo on startService throws an IllegalStateException, see https://developer.android.com/about/versions/oreo/android-8.0-changes
            startForegroundService(i);
        } else {
            startService(i);
        }

        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters jobParameters) {
        return true;
    }


}
