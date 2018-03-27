package de.tum.in.net.model;

import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;

import org.bouncycastle.util.encoders.Base64;

import de.tum.in.net.util.Tap;

/**
 * Created by johannes on 22.03.17.
 */

public class TlsResult implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6580999020716758862L;

  // required
  private final String ip;

  // optional
  private String receivedBytes;
  private String sentBytes;

  public TlsResult(Socket s, Tap t) {
    this(s.getInetAddress().getHostAddress(), t.getInputBytes(), t.getOutputytes());
  }

  public TlsResult(final String destination, final byte[] receivedBytes, final byte[] sentBytes) {
    this.ip = Objects.requireNonNull(destination, "destination bytes must not be null");

    if (receivedBytes != null) {
      this.receivedBytes = Base64.toBase64String(receivedBytes);
    }
    if (sentBytes != null) {
      this.sentBytes = Base64.toBase64String(sentBytes);
    }

  }


  /**
   * For a successful scenario it returns the bytes received. For an unsuccessful scenario it could
   * be null.
   *
   * @return the bytes sent.
   */
  public byte[] getSentBytesRaw() {
    return sentBytes == null ? null : Base64.decode(sentBytes);
  }

  /**
   * 
   * @return Base-64 encoded value, or null.
   */
  public String getSentBytes() {
    return sentBytes;
  }

  /**
   * 
   * @return Base-64 encoded value, or null.
   */
  public String getReceivedBytes() {
    return receivedBytes;
  }



  /**
   * For a successful scenario it returns the bytes received. For an unsuccessful scenario it could
   * be null.
   *
   * @return the bytes received.
   */
  public byte[] getReceivedBytesRaw() {
    return receivedBytes == null ? null : Base64.decode(receivedBytes);
  }

  public String getDestination() {
    return ip;
  }

  @Override
  public String toString() {
    return "ScenarioResult [" + ip + "]";
  }

}
