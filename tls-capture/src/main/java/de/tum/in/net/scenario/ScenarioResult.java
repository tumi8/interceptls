package de.tum.in.net.scenario;

import java.net.Socket;
import java.util.Objects;

import org.bouncycastle.util.encoders.Base64;

import de.tum.in.net.model.Tap;

/**
 * Created by johannes on 22.03.17.
 */

public class ScenarioResult {

  // required
  private final String source;
  private final String destination;
  private final boolean successfullyConnected;

  // optional
  private final String receivedBytes;
  private final String sentBytes;
  private final Throwable error;
  private final String msg;

  private ScenarioResult(final String source, final String destination, boolean success,
      final byte[] receivedBytes, final byte[] sentBytes, Throwable t, String msg) {
    this.source = source;
    this.destination = Objects.requireNonNull(destination, "destination bytes must not be null");
    this.successfullyConnected = success;
    // Objects.requireNonNull(receivedBytes, "received bytes must not be null");
    // Objects.requireNonNull(sentBytes, "sentBytes must not be null");
    this.receivedBytes = receivedBytes == null ? null : Base64.toBase64String(receivedBytes);
    this.sentBytes = sentBytes == null ? null : Base64.toBase64String(sentBytes);
    this.error = t;
    this.msg = msg;
  }



  public boolean isSuccess() {
    return successfullyConnected;
  }

  /**
   * For a successful scenario it returns the bytes received. For an unsuccessful scenario it could
   * be null.
   *
   * @return the bytes sent.
   */
  public byte[] getSentBytes() {
    return sentBytes == null ? null : Base64.decode(sentBytes);
  }

  /**
   * For a successful scenario it returns the bytes received. For an unsuccessful scenario it could
   * be null.
   *
   * @return the bytes received.
   */
  public byte[] getReceivedBytes() {
    return receivedBytes == null ? null : Base64.decode(receivedBytes);
  }

  public String getMsg() {
    ensureResult(false);
    return msg;
  }

  private void ensureResult(final boolean successful) {
    if (this.successfullyConnected != successful) {
      throw new IllegalStateException("The method call is not allowed.");
    }
  }

  public Throwable getError() {
    return error;
  }

  public String getDestination() {
    return destination;
  }

  public String getSource() {
    return source;
  }

  @Override
  public String toString() {
    return "ScenarioResult [" + source + "->" + destination + "] "
        + (successfullyConnected ? "success" : "failure");
  }

  public static class ScenarioResultBuilder {
    // always required
    private String source;
    private String destination;
    boolean successfullyConnected = true;

    // optional
    private byte[] receivedBytes;
    private byte[] sentBytes;
    private Throwable t;
    private String msg;

    public ScenarioResultBuilder(String source, String destination) {
      this.source = source;
      this.destination = destination;
    }

    public ScenarioResultBuilder(Socket s) {
      this.source = s.getLocalSocketAddress().toString().split("/")[1];
      this.destination = s.getRemoteSocketAddress().toString().split("/")[1];
    }

    public ScenarioResultBuilder sent(byte[] sentBytes) {
      this.sentBytes = sentBytes;
      return this;
    }

    public ScenarioResultBuilder received(byte[] receivedBytes) {
      this.receivedBytes = receivedBytes;
      return this;
    }

    public ScenarioResultBuilder error(Throwable t) {
      this.t = t;
      return this;
    }

    public ScenarioResultBuilder message(String msg) {
      this.msg = msg;
      return this;
    }

    public ScenarioResult connected() {
      this.successfullyConnected = true;
      return build();
    }

    public ScenarioResult notConnected() {
      this.successfullyConnected = false;
      return build();
    }

    private ScenarioResult build() {
      return new ScenarioResult(source, destination, successfullyConnected, receivedBytes,
          sentBytes, t, msg);
    }

    /**
     * @param tap - may be null
     * @return
     */
    public ScenarioResultBuilder transmitted(Tap tap) {
      if (tap != null) {
        this.receivedBytes = tap.getInputBytes();
        this.sentBytes = tap.getOutputytes();
      }
      return this;
    }



  }
}
