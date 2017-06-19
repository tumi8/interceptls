package de.tum.in.net.session;

import java.io.IOException;
import java.util.Objects;

import de.tum.in.net.model.AnalysisAPI;
import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class FixedIdTestSession implements TestSession {

  private final SessionID sessionID;
  private final AnalysisAPI analysisAPI;

  public FixedIdTestSession(final SessionID sessionID, final String ip) throws IOException {
    this.sessionID = Objects.requireNonNull(sessionID, "sessionID must not be null");
    this.analysisAPI = APIClient.createClient(ip).create(AnalysisAPI.class);
  }


  @Override
  public SessionID getSessionID() {
    return sessionID;
  }

  @Override
  public void uploadHandshake(int testCounter, ScenarioResult result) throws IOException {
    analysisAPI.uploadHandshake(new TestID(sessionID, testCounter), result).execute();
  }
}
