package de.tum.in.net.session;

import java.io.IOException;
import java.util.Objects;

import de.tum.in.net.model.AnalysisAPI;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class FixedIdTestSession implements TestSession {

  private final SessionId id;
  private final AnalysisAPI analysisAPI;

  public FixedIdTestSession(final SessionId id, final String ip) throws IOException {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.analysisAPI = APIClient.createClient(ip).create(AnalysisAPI.class);
  }


  @Override
  public SessionId getSessionID() {
    return id;
  }

  @Override
  public void uploadHandshake(final ScenarioResult result) throws IOException {
    analysisAPI.uploadHandshake(id, result).execute();
  }
}
