package de.tum.in.net.session;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.AnalysisResult;
import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class LoggingTestSession implements TestSession {

  private static final Logger log = LoggerFactory.getLogger(LoggingTestSession.class);
  private static final String ID = "LocalTestSession";

  @Override
  public SessionID getSessionID() {
    return new SessionID(ID);
  }

  @Override
  public void uploadHandshake(int testCounter, ScenarioResult result) throws IOException {
    log.info("testID: {}, result: {}", new TestID(ID, testCounter), result);
  }

  @Override
  public AnalysisResult getAnalysisResult(int testCounter) throws IOException {
    log.info("testID: {}", new TestID(ID, testCounter));
    return AnalysisResult.noInterception();

  }
}
