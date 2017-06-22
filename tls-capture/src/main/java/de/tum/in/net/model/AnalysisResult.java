package de.tum.in.net.model;

public class AnalysisResult {

  private final String diffSent;
  private final String diffRec;

  private AnalysisResult() {
    this.diffSent = null;
    this.diffRec = null;
  }


  private AnalysisResult(String diffSent, String diffRec) {
    this.diffSent = diffSent;
    this.diffRec = diffRec;
  }


  public static AnalysisResult noInterception() {
    return new AnalysisResult();
  }

  public static AnalysisResult intercepted(String diffSent, String diffRec) {
    return new AnalysisResult(diffSent, diffRec);

  }

  public boolean isIntercepted() {
    return diffRec != null || diffSent != null;
  }

  public String getDiffSent() {
    return diffSent;
  }

  public String getDiffRec() {
    return diffRec;
  }



}
