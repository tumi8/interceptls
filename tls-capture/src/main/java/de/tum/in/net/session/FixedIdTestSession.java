package de.tum.in.net.session;

import java.io.IOException;
import java.util.Collection;

import de.tum.in.net.model.AnalysisAPI;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class FixedIdTestSession implements TestSession {

  private static final String id = "1";
  private final AnalysisAPI analysisAPI;

  public FixedIdTestSession(final String ip) throws IOException {
    this.analysisAPI = APIClient.createClient(ip).create(AnalysisAPI.class);
  }


  @Override
  public String getSessionID() {
    return id;
  }

  @Override
  public void uploadHandshake(final Collection<ScenarioResult> results) throws IOException {
    for (final ScenarioResult result : results) {
      analysisAPI.uploadHandshake(id, result).execute();
    }
  }
}
