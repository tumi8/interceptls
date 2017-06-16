package de.tum.in.net.session;

import java.io.IOException;

import de.tum.in.net.model.AnalysisAPI;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;
import retrofit2.Response;

/**
 * Created by johannes on 04.04.17.
 */

public class OnlineTestSession implements TestSession {

  private final String id;
  private final AnalysisAPI analysisAPI;

  public OnlineTestSession(final String ip) throws IOException {
    this.analysisAPI = APIClient.createClient(ip).create(AnalysisAPI.class);
    final Response<SessionId> res = analysisAPI.newSessionID().execute();
    this.id = res.body().getID();
  }


  @Override
  public String getSessionID() {
    return this.id;
  }

  @Override
  public void uploadHandshake(final ScenarioResult result) throws IOException {
    analysisAPI.uploadHandshake(this.id, result).execute();
  }
}
