package de.tum.in.net.analysis;

import java.util.List;
import java.util.Objects;

public class AnalysisResult {

  private final NetworkStats stats;
  private final List<ProbedHostAnalysis> probedHosts;

  public AnalysisResult(NetworkStats stats, List<ProbedHostAnalysis> probedHosts) {
    this.stats = Objects.requireNonNull(stats);
    this.probedHosts = Objects.requireNonNull(probedHosts);
  }

  public List<ProbedHostAnalysis> getProbedHosts() {
    return probedHosts;
  }

  public NetworkStats getStats() {
    return stats;
  }


}
