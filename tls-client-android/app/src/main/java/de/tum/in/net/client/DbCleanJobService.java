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

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by johannes on 26.03.18.
 */

public class DbCleanJobService extends JobService {

    private static final Logger log = LoggerFactory.getLogger(DbCleanJobService.class);

    public static void init(final Context ctx) {
        final JobScheduler js = (JobScheduler) ctx.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        final JobInfo job = new JobInfo.Builder(
                JobId.dbCleanJobServiceId,
                new ComponentName(ctx, TlsJobService.class))
                .setPeriodic(24 * 60 * 60 * 1000) //every 24 hours
                .setRequiresCharging(true)
                .build();
        js.schedule(job);
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        log.debug("onStartJob DbCleanJobService");

        final TlsDB db = new TlsDB(this);
        db.deleteOldTests();

        return false;
    }

    @Override
    public boolean onStopJob(final JobParameters jobParameters) {
        return true;
    }


}
