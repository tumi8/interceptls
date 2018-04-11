package de.tum.in.net.model;

import java.io.IOException;
import java.util.List;

import de.tum.in.net.analysis.ProbedHostAnalysis;

public interface HandshakeAnalyser {

  List<ProbedHostAnalysis> analyse(TlsTestResult result) throws IOException;
}
