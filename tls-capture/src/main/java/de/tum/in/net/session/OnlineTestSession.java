package de.tum.in.net.session;

import java.io.IOException;
import java.util.List;

import de.tum.in.net.analysis.AnalysisAPI;
import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 04.04.17.
 */

public class OnlineTestSession implements TestSession {

  private final AnalysisAPI analysisAPI;

  public OnlineTestSession(final String ip) {
    this.analysisAPI = APIClient.createClient(ip).create(AnalysisAPI.class);
  }

  @Override
  public SessionID uploadResult(TlsTestResult result) throws IOException {
    return analysisAPI.uploadResult(result).execute().body();
  }

  @Override
  public List<AnalysisResult> getAnalysisResult(SessionID id) throws IOException {
    return analysisAPI.getAnalysis(id).execute().body();
  }

}
