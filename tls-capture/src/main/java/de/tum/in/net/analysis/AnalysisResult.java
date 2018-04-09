package de.tum.in.net.analysis;

import java.util.Objects;

public class AnalysisResult {

  private String target;
  private TlsState tlsState;
  private TlsMessageDiff clientHello;
  private TlsMessageDiff serverHello;
  private TlsMessageDiff certificate;

  private AnalysisResult(String target, TlsState tlsState) {
    this.target = Objects.requireNonNull(target);
    this.tlsState = Objects.requireNonNull(tlsState);
  }

  private AnalysisResult(String target, TlsMessageDiff clientHello, TlsMessageDiff serverHello,
      TlsMessageDiff certificate) {
    this.target = Objects.requireNonNull(target);
    this.tlsState = TlsState.INTERCEPTION;
    this.clientHello = Objects.requireNonNull(clientHello);
    this.serverHello = Objects.requireNonNull(serverHello);
    this.certificate = Objects.requireNonNull(certificate);
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

  public String getTarget() {
    return target;
  }


  public static AnalysisResult unknown(String target) {
    return new AnalysisResult(target, TlsState.UNKNOWN);
  }

  public static AnalysisResult noInterception(String target) {
    return new AnalysisResult(target, TlsState.NO_INTERCEPTION);
  }

  public static AnalysisResult intercepted(String target, TlsMessageDiff clientHello,
      TlsMessageDiff serverHello, TlsMessageDiff certificate) {
    return new AnalysisResult(target, clientHello, serverHello, certificate);
  }

  public boolean isIntercepted() {
    return TlsState.INTERCEPTION.equals(tlsState);
  }

  public TlsState getTlsState() {
    return tlsState;
  }


}
