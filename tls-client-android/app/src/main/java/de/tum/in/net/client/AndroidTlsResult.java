package de.tum.in.net.client;

import de.tum.in.net.model.TlsTestResult;

public class AndroidTlsResult {

    private final String timestamp;
    private final boolean uploaded;
    private final TlsTestResult result;

    public AndroidTlsResult(String timestamp, boolean uploaded, TlsTestResult result) {
        this.timestamp = timestamp;
        this.uploaded = uploaded;
        this.result = result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public TlsTestResult getResult() {
        return result;
    }
}
