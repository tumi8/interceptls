package de.tum.in.net.model;

import java.io.IOException;

import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.session.SessionId;

/**
 * Created by johannes on 04.04.17.
 */

public interface TestSession {

  /**
   * @return the id which is [A-Za-z0-9]+
   */
  SessionId getSessionID();

  // void uploadHandshake(Collection<ScenarioResult> results) throws IOException;

  void uploadHandshake(ScenarioResult result) throws IOException;
}
