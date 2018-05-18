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

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.model.TlsTestResult;

public class AndroidTlsResult {

    private final String timestamp;
    private final TlsTestResult testResult;
    private final AnalysisResult analysisResult;

    public AndroidTlsResult(final String timestamp, final TlsTestResult testResult, final AnalysisResult analysisResult) {
        this.timestamp = timestamp;
        this.testResult = testResult;
        this.analysisResult = analysisResult;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isUploaded() {
        return analysisResult != null;
    }

    public TlsTestResult getTestResult() {
        return testResult;
    }

    public AnalysisResult getAnalysisResult() {
        return analysisResult;
    }
}
