package de.tum.in.net.session;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class LoggingTestSession implements TestSession {

  private static final Logger log = LoggerFactory.getLogger(LoggingTestSession.class);

  @Override
  public SessionId getSessionID() {
    return new SessionId("LocalTestSession");
  }

  @Override
  public void uploadHandshake(final ScenarioResult result) throws IOException {
    log.info(result.toString());
  }
}
