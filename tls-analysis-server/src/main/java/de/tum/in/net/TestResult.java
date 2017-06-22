package de.tum.in.net;

import de.tum.in.net.scenario.ScenarioResult;

public class TestResult {

  private ScenarioResult clientResult;
  private ScenarioResult serverResult;

  public TestResult(ScenarioResult client, ScenarioResult server) {
    this.clientResult = client;
    this.serverResult = server;
  }

  public boolean hasClientResult() {
    return clientResult != null;
  }

  public boolean hasServerResult() {
    return serverResult != null;
  }

  public ScenarioResult getClientResult() {
    return clientResult;
  }

  public ScenarioResult getServerResult() {
    return serverResult;
  }

}
