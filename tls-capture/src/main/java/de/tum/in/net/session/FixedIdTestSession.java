package de.tum.in.net.session;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import de.tum.in.net.model.AnalysisAPI;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 04.04.17.
 */

public class FixedIdTestSession implements TestSession {

  private final String id;
  private final AnalysisAPI analysisAPI;

  public FixedIdTestSession(final String id, final String ip) throws IOException {
    this.id = Objects.requireNonNull(id, "id must not be null");
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
