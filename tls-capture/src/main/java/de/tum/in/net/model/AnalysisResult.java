package de.tum.in.net.model;

import java.util.Objects;

public class AnalysisResult {

  private AnalysisResultType type;
  private TlsMessageDiff clientHello;
  private TlsMessageDiff serverHello;
  private TlsMessageDiff certificate;
  private String error;

  private AnalysisResult(AnalysisResultType type) {
    this.type = Objects.requireNonNull(type);
  }

  private AnalysisResult(TlsMessageDiff clientHello, TlsMessageDiff serverHello,
      TlsMessageDiff certificate) {
    this.type = AnalysisResultType.INTERCEPTION;
    this.clientHello = Objects.requireNonNull(clientHello);
    this.serverHello = Objects.requireNonNull(serverHello);
    this.certificate = Objects.requireNonNull(certificate);
  }

  public AnalysisResult(String msg) {
    this.type = AnalysisResultType.ERROR;
    this.error = msg;
  }

  public TlsMessageDiff getClientHelloDiff() {
    return clientHello;
  }

  public TlsMessageDiff getServerHelloDiff() {
    return serverHello;
  }

  public TlsMessageDiff getCertificateDiff() {
    return certificate;
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

  public static AnalysisResult intercepted(TlsMessageDiff clientHello, TlsMessageDiff serverHello,
      TlsMessageDiff certificate) {
    return new AnalysisResult(clientHello, serverHello, certificate);
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
