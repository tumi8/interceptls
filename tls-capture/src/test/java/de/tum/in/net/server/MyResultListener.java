package de.tum.in.net.server;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 17.05.17.
 */

public class MyResultListener implements ResultListener<ScenarioResult> {

  public ScenarioResult result;

  @Override
  public void publish(final ScenarioResult result) {
    this.result = result;
  }
}
