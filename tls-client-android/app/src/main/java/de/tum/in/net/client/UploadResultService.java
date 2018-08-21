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

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsConstants;
import de.tum.in.net.model.TlsTestResult;
import de.tum.in.net.session.OnlineTestSession;

/**
 * Created by johannes on 21.03.18.
 */

public class UploadResultService extends IntentService {

    private static final Logger log = LoggerFactory.getLogger(UploadResultService.class);

    public UploadResultService() {
        super("UploadResultService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        if (!ConfigurationReader.isDataCollectionAllowed(this)) {
            log.info("Won't upload the data because the user has not given his permission.");
            return;
        }

        final TlsDB db = new TlsDB(this);

        final List<TlsTestResult> results = db.getNotUploadedResults();

        final TestSession s = new OnlineTestSession(TlsConstants.TLS_ANALYSIS_URL);
        try {
            for (final TlsTestResult result : results) {
                final AnalysisResult analysisResult = s.uploadResult(result);

                db.uploadedResult(result.getTimestamp(), analysisResult);
            }
            log.info("All results successfully uploaded to analysis server");
        } catch (final IOException e) {
            log.warn("Could not upload results to analysis server", e);
        }

    }

}
