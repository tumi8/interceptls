package de.tum.in.net.model;

import java.io.IOException;

import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.session.SessionID;

/**
 * Created by johannes on 04.04.17.
 */

public interface TestSession {

  /**
   * @return the sessionID which is [A-Za-z0-9]+
   */
  SessionID getSessionID();

  void uploadHandshake(int testCounter, ScenarioResult result) throws IOException;

  AnalysisResult getAnalysisResult(int testCounter) throws IOException;
}
