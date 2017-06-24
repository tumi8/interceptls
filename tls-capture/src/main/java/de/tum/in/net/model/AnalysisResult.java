package de.tum.in.net.model;

import java.util.Objects;

public class AnalysisResult {

  private AnalysisResultType type;
  private final String diffSent;
  private final String diffRec;
  private final String errorMsg;

  private AnalysisResult(AnalysisResultType type) {
    this.type = Objects.requireNonNull(type);
    this.diffSent = null;
    this.diffRec = null;
    this.errorMsg = null;
  }


  private AnalysisResult(String diffSent, String diffRec) {
    this.type = AnalysisResultType.INTERCEPTION;
    this.diffSent = Objects.requireNonNull(diffSent);
    this.diffRec = Objects.requireNonNull(diffRec);
    this.errorMsg = null;
  }

  public AnalysisResult(String msg) {
    this.type = AnalysisResultType.ERROR;
    this.diffSent = null;
    this.diffRec = null;
    this.errorMsg = msg;
  }


  public static AnalysisResult noClientResult() {
    return new AnalysisResult(AnalysisResultType.NO_CLIENT_RESULT);
  }

  public static AnalysisResult noServerResult() {
    return new AnalysisResult(AnalysisResultType.NO_SERVER_RESULT);
  }

  public static AnalysisResult noClientNoServerResult() {
    return new AnalysisResult(AnalysisResultType.NO_CLIENT_NO_SERVER_RESULT);
  }

  public static AnalysisResult noInterception() {
    return new AnalysisResult(AnalysisResultType.NO_INTERCEPTION);
  }

  public static AnalysisResult error(String msg) {
    return new AnalysisResult(msg);
  }

  public static AnalysisResult intercepted(String diffSent, String diffRec) {
    return new AnalysisResult(diffSent, diffRec);
  }

  public boolean isIntercepted() {
    return AnalysisResultType.INTERCEPTION.equals(type);
  }

  public AnalysisResultType getType() {
    return type;
  }

  public String getDiffSent() {
    return diffSent;
  }

  public String getDiffRec() {
    return diffRec;
  }



}
