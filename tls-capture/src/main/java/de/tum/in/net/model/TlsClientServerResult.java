package de.tum.in.net.model;

import java.io.Serializable;
import java.util.Objects;

import de.tum.in.net.client.HostAndPort;

public class TlsClientServerResult implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -1761171388454073921L;

  private final HostAndPort target;
  private final State state;
  private final TlsResult client, server;

  private TlsClientServerResult(HostAndPort target, TlsResult client, TlsResult server,
      State state) {
    this.target = Objects.requireNonNull(target, "target must not be null");
    this.state = Objects.requireNonNull(state, "state must not be null");

    // some results are only required depending on the state
    switch (state) {
      case CONNECTED:
      case ERROR:
        Objects.requireNonNull(client);
        break;

      case NO_CONNECTION:
        break;
    }


    this.server = server;
    this.client = client;
  }

  public TlsResult getClientResult() {
    return this.client;
  }

  public TlsResult getServerResult() {
    return this.server;
  }

  public State getState() {
    return state;
  }

  /**
   * 
   * @return true if a connection was successfully established
   */
  public boolean isSuccess() {
    return State.CONNECTED.equals(state);
  }

  public static TlsClientServerResult connected(HostAndPort target, TlsResult client,
      TlsResult server) {
    return new TlsClientServerResult(target, client, server, State.CONNECTED);
  }

  public static TlsClientServerResult error(HostAndPort target, TlsResult client) {
    return new TlsClientServerResult(target, client, null, State.ERROR);
  }

  public static TlsClientServerResult noConnection(HostAndPort target) {
    return new TlsClientServerResult(target, null, null, State.NO_CONNECTION);
  }

  /**
   * Can only be called if isSuccess returns true.
   * 
   * @return
   */
  public boolean isIntercepted() {
    if (!isSuccess()) {
      throw new IllegalStateException("Can only be called if isSuccess() is true.");
    }
    return !(client.getSentBytes().equals(server.getReceivedBytes())
        && client.getReceivedBytes().equals(server.getSentBytes()));
  }

  public HostAndPort getHostAndPort() {
    return target;
  }

}
