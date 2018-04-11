package de.tum.in.net.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.analysis.ProbedHostAnalysis;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestResult;

/**
 * Created by johannes on 04.04.17.
 */

public class LoggingTestSession implements TestSession {

  private static final Logger log = LoggerFactory.getLogger(LoggingTestSession.class);

  @Override
  public AnalysisResult uploadResult(TlsTestResult result) throws IOException {
    log.info("upload: {}", result);
    List<ProbedHostAnalysis> r = new ArrayList<>();
    return new AnalysisResult(new NetworkStats(), r);
  }


}
