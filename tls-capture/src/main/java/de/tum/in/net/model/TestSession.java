package de.tum.in.net.model;

import java.io.IOException;

import de.tum.in.net.analysis.AnalysisResult;

/**
 * Created by johannes on 04.04.17.
 */

public interface TestSession {

  AnalysisResult uploadResult(TlsTestResult result) throws IOException;

}
