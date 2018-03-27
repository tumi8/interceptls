package de.tum.in.net.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDateTime;

public class TlsTestResult implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = 5955734350476053070L;

  private String timestamp = LocalDateTime.now().toString();
  private NetworkId network;
  private List<TlsClientServerResult> results;

  public TlsTestResult(NetworkId network, List<TlsClientServerResult> results) {
    this.network = Objects.requireNonNull(network);
    this.results = Objects.requireNonNull(results);
  }

  public boolean anySuccessfulConnection() {
    return successfulConnections() > 0;
  }

  public int successfulConnections() {
    int counter = 0;
    for (TlsClientServerResult result : results) {
      if (result.isSuccess()) {
        counter++;
      }
    }
    return counter;
  }

  public int interceptions() {
    int counter = 0;
    for (TlsClientServerResult result : results) {
      if (result.isSuccess()) {
        if (result.isIntercepted()) {
          counter++;
        }
      }
    }
    return counter;
  }

  public boolean anyInterception() {
    return interceptions() > 0;
  }

  public NetworkId getNetworkId() {
    return network;
  }

  public List<TlsClientServerResult> getClientServerResults() {
    return Collections.unmodifiableList(results);
  }

  public String getTimestamp() {
    return timestamp;
  }

}
