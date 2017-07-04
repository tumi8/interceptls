package de.tum.in.net.model;

import java.util.List;

public class TLSHandshake {

  // those types and names must match the json from tls-json-parser
  private TlsMessageType type;
  private int version;
  private int cipher;
  private int[] ciphers;
  private String[] cert_chain;
  private Extensions ext;


  public int[] getCiphers() {
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

  public TlsMessageDiff createDiff(List<TLSHandshake> messages_sent) {
    TLSHandshake other = null;
    for (TLSHandshake h : messages_sent) {
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
        return new TlsMessageDiff(version, ciphers, ext.createDiff(other.ext));

      case ServerHello:
        Diff cipher = new Diff(this.cipher, other.cipher);
        return new TlsMessageDiff(version, cipher, ext.createDiff(other.ext));

      case Certificate:
        Diff certChain = new Diff(this.cert_chain, other.cert_chain);
        return new TlsMessageDiff(certChain);

      default:
        throw new IllegalStateException("Unknwon type: " + type);

    }

  }

}
