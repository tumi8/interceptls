package de.tum.in.net.session;

import java.io.IOException;

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
  public AnalysisResult uploadResult(TlsTestResult result) throws IOException {
    return analysisAPI.uploadResult(result).execute().body();
  }

}
