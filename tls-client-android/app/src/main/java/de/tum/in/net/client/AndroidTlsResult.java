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
