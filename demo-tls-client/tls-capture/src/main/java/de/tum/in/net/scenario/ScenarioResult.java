package de.tum.in.net.scenario;

import java.util.Objects;

/**
 * Created by johannes on 22.03.17.
 */

public class ScenarioResult {

    private final boolean success;

    //success
    private byte[] receivedBytes;
    private byte[] sentBytes;

    //error
    private String msg;
    private Throwable cause;

    /**
     * Scenario was successful.
     *
     * @param receivedBytes
     * @param sentBytes
     */
    public ScenarioResult(byte[] receivedBytes, byte[] sentBytes) {
        this.success = true;
        this.receivedBytes = Objects.requireNonNull(receivedBytes, "received bytes must not be null");
        this.sentBytes = Objects.requireNonNull(sentBytes, "sentBytes must not be null");
    }

    /**
     * An error occurred during the scenario.
     *
     * @param msg
     * @param cause
     */
    public ScenarioResult(String msg, Throwable cause) {
        this.success = false;
        this.msg = Objects.requireNonNull(msg, "msg must not be null");
        this.cause = Objects.requireNonNull(cause, "cause must not be null");
    }

    public boolean isSuccess() {
        return success;
    }

    public byte[] getSentBytes() {
        return sentBytes;
    }

    public byte[] getReceivedBytes() {
        return receivedBytes;
    }
}
