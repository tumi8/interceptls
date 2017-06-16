package de.tum.in.net.server;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.session.SessionId;

/**
 * Created by johannes on 17.05.17.
 */

public class MyResultListener implements ResultListener<ScenarioResult> {

  public SessionId id;
  public ScenarioResult result;

  @Override
  public void publish(SessionId id, ScenarioResult result) {
    this.id = id;
    this.result = result;
  }

}
