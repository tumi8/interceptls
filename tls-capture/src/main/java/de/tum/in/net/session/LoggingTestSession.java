package de.tum.in.net.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class LoggingTestSession implements TestSession {

  private static final Logger log = LoggerFactory.getLogger(LoggingTestSession.class);

  @Override
  public String getSessionID() {
    return "LocalTestSession";
  }

  @Override
  public void uploadHandshake(final Collection<ScenarioResult> results) throws IOException {
    log.debug("Logging {} results", results.size());
    for (final ScenarioResult result : results) {
      log.info(result.toString());
    }
  }
}
