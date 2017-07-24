package de.tum.in.net.model;

import java.io.Closeable;

import de.tum.in.net.TestResult;
import de.tum.in.net.scenario.ScenarioResult;

public interface DatabaseService extends Closeable {

  String newSessionID();

  void addResult(TestID id, ScenarioResult result);

  TestResult getResult(TestID id);
}
