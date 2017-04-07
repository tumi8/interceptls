package de.tum.in.net.scenario;

import java.util.Objects;

import de.tum.in.net.Tap;

/**
 * Created by johannes on 22.03.17.
 */

public class ScenarioResult {

    private final boolean success;
    private String destination;

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
    public ScenarioResult(final String destination, final byte[] receivedBytes, final byte[] sentBytes) {
        this.success = true;
        this.destination = Objects.requireNonNull(destination, "destination bytes must not be null");
        this.receivedBytes = Objects.requireNonNull(receivedBytes, "received bytes must not be null");
        this.sentBytes = Objects.requireNonNull(sentBytes, "sentBytes must not be null");
    }

    /**
     * An error occurred during the scenario.
     *
     * @param msg
     * @param cause
     */
    public ScenarioResult(final String msg, final Throwable cause, final byte[] receivedBytes, final byte[] sentBytes) {
        this.success = false;
        this.msg = Objects.requireNonNull(msg, "msg must not be null");
        this.cause = Objects.requireNonNull(cause, "cause must not be null");
        this.receivedBytes = receivedBytes;
        this.sentBytes = sentBytes;
    }

    /**
     * An error occurred during the scenario.
     *
     * @param msg
     * @param cause
     */
    public ScenarioResult(final String destination, final String msg, final Throwable cause, final Tap tap) {
        this.success = false;
        this.destination = Objects.requireNonNull(destination, "destination bytes must not be null");
        this.msg = Objects.requireNonNull(msg, "msg must not be null");
        this.cause = Objects.requireNonNull(cause, "cause must not be null");
        if (tap != null) {
            this.receivedBytes = tap.getInputBytes();
            this.sentBytes = tap.getOutputytes();
        }
    }


    public boolean isSuccess() {
        return success;
    }

    /**
     * For a successful scenario it returns the bytes received.
     * For an unsuccessful scenario it could be null.
     *
     * @return the bytes sent.
     */
    public byte[] getSentBytes() {
        return sentBytes;
    }

    /**
     * For a successful scenario it returns the bytes received.
     * For an unsuccessful scenario it could be null.
     *
     * @return the bytes received.
     */
    public byte[] getReceivedBytes() {
        return receivedBytes;
    }

    public String getMsg() {
        ensureResult(false);
        return msg;
    }

    private void ensureResult(final boolean successful) {
        if (this.success != successful) {
            throw new IllegalStateException("The method call is not allowed.");
        }
    }

    public Throwable getCause() {
        ensureResult(false);
        return cause;
    }

    public String getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Destination: " + destination + " " + (success ? "success" : "failure");
    }
}
