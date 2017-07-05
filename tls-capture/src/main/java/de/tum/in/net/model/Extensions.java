package de.tum.in.net.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Extensions {

  private int[] ecPointFormats;
  private int[] ellipticCurves;
  private Integer heartbeat;
  private List<int[]> signatureAlgorithms;
  // private signedCertificateTimestamp
  private List<SNI> sni;

  public int[] getEcPointFormats() {
    return ecPointFormats;
  }

  public int[] getEllipticCurves() {
    return ellipticCurves;
  }

  public Map<String, Diff> createDiff(Extensions ext) {
    Map<String, Diff> diffs = new HashMap<>();

    diffs.put("ecPointFormats", new Diff(ecPointFormats, ext.ecPointFormats));
    diffs.put("ellipticCurves", new Diff(ellipticCurves, ext.ellipticCurves));
    diffs.put("heartbeat", new Diff(heartbeat, ext.heartbeat));
    diffs.put("sni", new Diff(sni, ext.sni));
    // diffs.add(new Diff("signatureAlgorithms", signatureAlgorithms, ext.signatureAlgorithms));

    return diffs;
  }

}
