package de.tum.in.net.model;

import java.util.Collections;
import java.util.Map;

public class TlsMessageDiff {

  private Diff version;
  private Diff ciphers;
  private Map<String, Diff> extDiffs;
  private Diff certChain;


  /*
   * Client Hello / Server Hello Diff
   */
  public TlsMessageDiff(Diff version, Diff ciphers, Map<String, Diff> extDiffs) {
    this.version = version;
    this.ciphers = ciphers;
    this.extDiffs = extDiffs;
  }

  public TlsMessageDiff(Diff certChain) {
    this.certChain = certChain;
  }

  public Diff getVersionDiff() {
    return version;
  }

  public Diff getCiphersDiff() {
    return ciphers;
  }

  public Diff getCertChainDiff() {
    return certChain;
  }

  public Map<String, Diff> getExtensionsDiff() {
    return Collections.unmodifiableMap(extDiffs);
  }

}
