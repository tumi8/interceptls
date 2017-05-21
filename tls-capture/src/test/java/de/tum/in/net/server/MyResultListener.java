package de.tum.in.net.server;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.Severity;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 17.05.17.
 */

public class MyResultListener implements ResultListener<ScenarioResult> {

  public Severity severity;
  public ScenarioResult result;

  @Override
  public void publish(final Severity severity, final ScenarioResult result) {
    this.severity = severity;
    this.result = result;
  }
}
