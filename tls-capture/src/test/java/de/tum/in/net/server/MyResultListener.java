package de.tum.in.net.server;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 17.05.17.
 */

public class MyResultListener implements ResultListener<ScenarioResult> {

  public TestID testID;
  public ScenarioResult result;

  @Override
  public void publish(TestID id, ScenarioResult result) {
    this.testID = id;
    this.result = result;
  }


}
