package de.tum.in.net.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Extensions {

  private int[] ecPointFormats;
  private int[] ellipticCurves;
  private Integer heartbeat;
  private List<List<Integer>> signatureAlgorithms;
  // private signedCertificateTimestamp
  private List<SNI> sni;

  public int[] getEcPointFormats() {
    return ecPointFormats;
  }

  public int[] getEllipticCurves() {
    return ellipticCurves;
  }

  public List<List<Integer>> getSignatureAlgorithms() {
    return signatureAlgorithms;
  }

  public Map<String, Diff> createDiff(Extensions ext) {
    Map<String, Diff> diffs = new HashMap<>();

    diffs.put("ecPointFormats", new Diff(ecPointFormats, ext.ecPointFormats));
    diffs.put("ellipticCurves", new Diff(ellipticCurves, ext.ellipticCurves));
    diffs.put("heartbeat", new Diff(heartbeat, ext.heartbeat));
    diffs.put("signatureAlgorithms", new Diff(signatureAlgorithms, ext.signatureAlgorithms));
    diffs.put("sni", new Diff(sni, ext.sni));

    return diffs;
  }

}
