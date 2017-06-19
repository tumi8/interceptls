package de.tum.in.net.session;

import java.io.IOException;

import de.tum.in.net.model.AnalysisAPI;
import de.tum.in.net.model.TestID;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;
import retrofit2.Response;

/**
 * Created by johannes on 04.04.17.
 */

public class OnlineTestSession implements TestSession {

  private final SessionID sessionId;
  private final AnalysisAPI analysisAPI;

  public OnlineTestSession(final String ip) throws IOException {
    this.analysisAPI = APIClient.createClient(ip).create(AnalysisAPI.class);
    final Response<SessionID> res = analysisAPI.newSessionID().execute();
    this.sessionId = res.body();
  }

  @Override
  public SessionID getSessionID() {
    return this.sessionId;
  }


  @Override
  public void uploadHandshake(int testCounter, ScenarioResult result) throws IOException {
    analysisAPI.uploadHandshake(new TestID(sessionId, testCounter), result).execute();
  }
}
