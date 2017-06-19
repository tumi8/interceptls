package de.tum.in.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.ScenarioResult;

public class LogResultListener implements ResultListener<ScenarioResult> {

  private static final Logger log = LogManager.getLogger();

  @Override
  public void publish(TestID id, ScenarioResult result) {
    log.info("TestID {}, result: {}", id, result);
  }

}
