package de.tum.in.net.model;

import java.util.ArrayList;
import java.util.List;

public class TLSHandshake {

  private TlsMessageType type;
  private String version;
  private int[] ciphers;
  private Extensions ext;


  public int[] getCiphers() {
    return ciphers;
  }

  public TlsMessageType getType() {
    return type;
  }

  public String getVersion() {
    return version;
  }

  public Extensions getExtensions() {
    return ext;
  }

  public List<Diff> createDiff(List<TLSHandshake> messages_sent) {
    TLSHandshake other = messages_sent.stream().filter(m -> m.type.equals(this.type)).findFirst()
        .orElseThrow(() -> new IllegalStateException("Could not find msg for type " + this.type));

    List<Diff> diffs = new ArrayList<>();

    switch (this.type) {
      case ClientHello:
        diffs.add(new Diff("TLS-Version", version, other.version));
        diffs.add(new Diff("Cipher", ciphers, other.ciphers));
        diffs.addAll(ext.createDiff(other.ext));
        break;


      default:
        throw new IllegalStateException("Unknwon type: " + type);

    }
    return diffs;

  }

}
