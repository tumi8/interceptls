package de.tum.in.net.analysis;

import java.util.Objects;

public class AnalysisResult {

  private TlsState tlsState;
  private TlsMessageDiff clientHello;
  private TlsMessageDiff serverHello;
  private TlsMessageDiff certificate;

  private AnalysisResult(TlsState tlsState) {
    this.tlsState = Objects.requireNonNull(tlsState);
  }

  private AnalysisResult(TlsMessageDiff clientHello, TlsMessageDiff serverHello,
      TlsMessageDiff certificate) {
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


  public static AnalysisResult unknown() {
    return new AnalysisResult(TlsState.UNKNOWN);
  }

  public static AnalysisResult noInterception() {
    return new AnalysisResult(TlsState.NO_INTERCEPTION);
  }

  public static AnalysisResult intercepted(TlsMessageDiff clientHello, TlsMessageDiff serverHello,
      TlsMessageDiff certificate) {
    return new AnalysisResult(clientHello, serverHello, certificate);
  }

  public boolean isIntercepted() {
    return TlsState.INTERCEPTION.equals(tlsState);
  }

  public TlsState getTlsState() {
    return tlsState;
  }


}
