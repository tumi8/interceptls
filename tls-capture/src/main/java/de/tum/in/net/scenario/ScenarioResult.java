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
  private final State state;

  // optional
  private final String receivedBytes;
  private final String sentBytes;
  private final Throwable error;
  private final String msg;

  private ScenarioResult(final String source, final String destination, State state,
      final byte[] receivedBytes, final byte[] sentBytes, Throwable t, String msg) {
    this.source = source;
    this.destination = Objects.requireNonNull(destination, "destination bytes must not be null");
    this.state = Objects.requireNonNull(state, "state must not be null");
    // Objects.requireNonNull(receivedBytes, "received bytes must not be null");
    // Objects.requireNonNull(sentBytes, "sentBytes must not be null");
    this.receivedBytes = receivedBytes == null ? null : Base64.toBase64String(receivedBytes);
    this.sentBytes = sentBytes == null ? null : Base64.toBase64String(sentBytes);
    this.error = t;
    this.msg = msg;
  }

  public State getState() {
    return state;
  }

  public boolean isSuccess() {
    return State.CONNECTED.equals(state);
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
    return msg;
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
    return "ScenarioResult [" + source + "->" + destination + "] " + state;
  }

  public static class ScenarioResultBuilder {
    // always required
    private String source;
    private String destination;
    private State state;

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


    public ScenarioResultBuilder message(String msg) {
      this.msg = msg;
      return this;
    }

    public ScenarioResult connected() {
      this.state = State.CONNECTED;
      return build();
    }

    public ScenarioResult noConnection() {
      this.state = State.NO_CONNECTION;
      return build();
    }

    public ScenarioResult error(Throwable t) {
      this.t = t;
      this.state = State.ERROR;
      return build();
    }


    private ScenarioResult build() {
      return new ScenarioResult(source, destination, state, receivedBytes, sentBytes, t, msg);
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
