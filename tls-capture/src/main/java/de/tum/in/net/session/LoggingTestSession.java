package de.tum.in.net.session;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class LoggingTestSession implements TestSession {

  private static final Logger log = LoggerFactory.getLogger(LoggingTestSession.class);
  private int counter = 0;

  @Override
  public SessionID getSessionID() {
    return new SessionID("LocalTestSession");
  }

  @Override
  public void uploadHandshake(int testCounter, ScenarioResult result) throws IOException {
    counter++;
    log.info("testID: {}, result: {}", new TestID("LocalTestSession", counter), result);
  }
}
