package de.tum.in.net.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AnalysisResult {

  private AnalysisResultType type;
  private List<Diff> clientHello;
  private String error;

  private AnalysisResult(AnalysisResultType type) {
    this.type = Objects.requireNonNull(type);
    this.clientHello = null;
  }

  private AnalysisResult(List<Diff> clientHello) {
    this.type = AnalysisResultType.INTERCEPTION;
    this.clientHello = Objects.requireNonNull(clientHello);
  }

  public AnalysisResult(String msg) {
    this.type = AnalysisResultType.ERROR;
    this.error = msg;
  }

  public List<Diff> getClientHelloDiffs() {
    return Collections.unmodifiableList(clientHello);
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

  public static AnalysisResult intercepted(List<Diff> diffs) {
    return new AnalysisResult(diffs);
  }

  public boolean isIntercepted() {
    return AnalysisResultType.INTERCEPTION.equals(type);
  }

  public AnalysisResultType getType() {
    return type;
  }

  public String getErrorMsg() {
    return error;
  }



}
