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
import android.app.job.JobScheduler;
import android.content.Context;

import java.util.Objects;

public class Util {

    private Util() {
        //utility
    }

    public static String formatTimestamp(final String iso8601) {
        Objects.requireNonNull(iso8601);
        return iso8601.replace("T", " ").substring(0, iso8601.length() - 4);
    }

    public static boolean isJobServiceOn(Context context, int JOB_ID) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService( Context.JOB_SCHEDULER_SERVICE );

        boolean hasBeenScheduled = false ;

        for ( JobInfo jobInfo : scheduler.getAllPendingJobs() ) {
            if ( jobInfo.getId() == JOB_ID ) {
                hasBeenScheduled = true ;
                break ;
            }
        }

        return hasBeenScheduled ;
    }
}
