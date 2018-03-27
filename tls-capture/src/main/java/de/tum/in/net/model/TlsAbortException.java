package de.tum.in.net.model;

import java.util.Objects;

public class TlsAbortException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -8496593340666315062L;

  private final TlsResult result;

  public TlsAbortException(TlsResult result, Throwable t) {
    this(result, null, t);
  }

  public TlsAbortException(TlsResult result, String msg, Throwable t) {
    super(msg, t);
    this.result = Objects.requireNonNull(result);
  }

  public TlsResult getTlsResult() {
    return this.result;
  }

}
