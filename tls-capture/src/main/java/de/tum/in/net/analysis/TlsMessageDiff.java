package de.tum.in.net.analysis;

import java.util.Collections;
import java.util.Map;

public class TlsMessageDiff {

  private Diff version;
  private Diff ciphers;
  private Diff compression;
  private Map<String, Diff> extDiffs;
  private Diff certChain;


  /*
   * Client Hello / Server Hello Diff
   */
  public TlsMessageDiff(Diff version, Diff ciphers, Diff compression, Map<String, Diff> extDiffs) {
    this.version = version;
    this.ciphers = ciphers;
    this.compression = compression;
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

  public Diff getCompressionDiff() {
    return compression;
  }

  public Diff getCertChainDiff() {
    return certChain;
  }

  public Map<String, Diff> getExtensionsDiff() {
    return Collections.unmodifiableMap(extDiffs);
  }

}
