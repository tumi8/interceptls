package de.tum.in.net.model;

import java.io.IOException;
import java.util.Collection;

import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public interface TestSession {

  /**
   * @return the id which is [A-Za-z0-9]+
   */
  String getSessionID();

  void uploadHandshake(Collection<ScenarioResult> results) throws IOException;
}
