package de.tum.in.net;

import de.tum.in.net.model.TlsResult;

public class TestResult {

  private TlsResult clientResult;
  private TlsResult serverResult;

  public TestResult(TlsResult client, TlsResult server) {
    this.clientResult = client;
    this.serverResult = server;
  }

  public boolean hasClientResult() {
    return clientResult != null;
  }

  public boolean hasServerResult() {
    return serverResult != null;
  }

  public TlsResult getClientResult() {
    return clientResult;
  }

  public TlsResult getServerResult() {
    return serverResult;
  }

}
