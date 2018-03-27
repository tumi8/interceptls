package de.tum.in.net.model;

import java.io.IOException;
import java.util.List;

import de.tum.in.net.analysis.AnalysisResult;
import de.tum.in.net.session.SessionID;

/**
 * Created by johannes on 04.04.17.
 */

public interface TestSession {

  SessionID uploadResult(TlsTestResult result) throws IOException;

  List<AnalysisResult> getAnalysisResult(SessionID id) throws IOException;
}
