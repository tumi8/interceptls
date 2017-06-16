package de.tum.in.net.scenario;

import java.net.Socket;
import java.util.Objects;

import org.bouncycastle.util.encoders.Base64;

import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TestId;

/**
 * Created by johannes on 22.03.17.
 */

public class ScenarioResult {

  // required
  private final TestId testId;
  private final String source;
  private final String destination;
  private final State state;

  // optional
  private String receivedBytes;
  private String sentBytes;
  private Throwable error;
  private String msg;

  private ScenarioResult(TestId testId, final String source, final String destination, State state,
      final byte[] receivedBytes, final byte[] sentBytes, Throwable t, String msg) {
    this.testId = Objects.requireNonNull(testId, "testId must not be null");
    this.source = source;
    this.destination = Objects.requireNonNull(destination, "destination bytes must not be null");
    this.state = Objects.requireNonNull(state, "state must not be null");

    // depending on state additional parameter are required
    switch (this.state) {
      case CONNECTED:
        this.receivedBytes = Base64.toBase64String(
            Objects.requireNonNull(receivedBytes, "receivedBytes must not be null"));
        this.sentBytes =
            Base64.toBase64String(Objects.requireNonNull(sentBytes, "sentBytes must not be null"));
        break;

      case NO_CONNECTION:
        // TODO no
        break;

      case ERROR:
        // this.msg = Objects.requireNonNull(msg, "msg must not be null");
        this.error = Objects.requireNonNull(t, "error must not be null");
        break;

      default:
        throw new IllegalStateException("Unknown state: " + this.state);

    }

  }

  public TestId getTestId() {
    return testId;
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
    private TestId testId;

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

    public ScenarioResult connected(TestId testId) {
      this.state = State.CONNECTED;
      this.testId = testId;
      return build();
    }

    public ScenarioResult noConnection() {
      this.state = State.NO_CONNECTION;
      return build();
    }

    public ScenarioResult error(Throwable t, TestId testId) {
      this.t = t;
      this.testId = testId;
      this.state = State.ERROR;
      return build();
    }


    private ScenarioResult build() {
      return new ScenarioResult(testId, source, destination, state, receivedBytes, sentBytes, t,
          msg);
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
