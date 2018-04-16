package de.tum.in.net.analysis;

import java.util.List;

public class TlsMessage {

  // those types and names must match the json from tls-json-parser
  private TlsMessageType type;
  private int version;
  private int[] compressions;
  private int compression;
  private int cipher;
  private int[] ciphers;
  private String[] cert_chain;
  private Extensions ext;

  private void isMessage(TlsMessageType type) {
    if (!this.type.equals(type)) {
      throw new IllegalStateException("Cannot get this parameter for this TLS Message Type");
    }
  }

  public int getCompression() {
    isMessage(TlsMessageType.ServerHello);
    return compression;
  }

  public int[] getCompressions() {
    isMessage(TlsMessageType.ClientHello);
    return compressions;
  }

  public int getCipher() {
    isMessage(TlsMessageType.ServerHello);
    return cipher;
  }

  public int[] getCiphers() {
    isMessage(TlsMessageType.ClientHello);
    return ciphers;
  }

  public TlsMessageType getType() {
    return type;
  }

  public String getVersion() {
    switch (version) {
      case 0x0300:
        return "SSLv3";
      case 0x0301:
        return "TLSv1.0";
      case 0x0302:
        return "TLSv1.1";
      case 0x0303:
        return "TLSv1.2";
      case 0x0304:
        return "TLSv1.3";

      default:
        return Integer.toString(version);
    }

  }

  public Extensions getExtensions() {
    return ext;
  }

  public TlsMessageDiff createDiff(List<TlsMessage> messages_sent) {
    TlsMessage other = null;
    for (TlsMessage h : messages_sent) {
      if (this.type.equals(h.type)) {
        other = h;
        break;
      }
    }
    if (other == null) {
      throw new IllegalStateException("Could not find msg for type " + this.type);
    }

    Diff version = new Diff(this.getVersion(), other.getVersion());
    switch (this.type) {
      case ClientHello:
        Diff ciphers = new Diff(this.ciphers, other.ciphers);
        Diff compressions = new Diff(this.compressions, other.compressions);
        return new TlsMessageDiff(version, ciphers, compressions, ext.createDiff(other.ext));

      case ServerHello:
        Diff cipher = new Diff(this.cipher, other.cipher);
        Diff compression = new Diff(this.compression, other.compression);
        return new TlsMessageDiff(version, cipher, compression, ext.createDiff(other.ext));

      case Certificate:
        Diff certChain = new Diff(this.cert_chain, other.cert_chain);
        return new TlsMessageDiff(certChain);

      default:
        throw new IllegalStateException("Unknwon type: " + type);

    }

  }

}
