package de.tum.in.net.client;

import java.io.Serializable;
import java.util.Objects;

public class HostAndPort implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -873403134912333123L;
  private final String host;
  private final int port;

  /**
   * Create a new target with default port 443 (HTTPS).
   * 
   * @param host
   */
  public HostAndPort(String host) {
    this(host, 443);
  }

  public HostAndPort(String host, int port) {
    this.host = Objects.requireNonNull(host);
    if (port < 0 || port > 65535) {
      throw new IllegalArgumentException("port must be between 0 and 65535");
    }
    this.port = port;
  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  @Override
  public String toString() {
    return host + ":" + port;
  }
}
