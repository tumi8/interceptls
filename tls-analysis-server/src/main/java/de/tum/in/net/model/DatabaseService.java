package de.tum.in.net.model;

import de.tum.in.net.TestResult;
import de.tum.in.net.scenario.ScenarioResult;

public interface DatabaseService {

  String newSessionID();

  void addResult(TestID id, ScenarioResult result);

  TestResult getResult(TestID id);
}
