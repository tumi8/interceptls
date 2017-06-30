package de.tum.in.net.model;

import java.util.ArrayList;
import java.util.List;

public class Extensions {

  private int[] ecPointFormats;
  private int[] ellipticCurves;
  private Integer heartbeat;
  private List<int[]> signatureAlgorithms;
  // private signedCertificateTimestamp

  public int[] getEcPointFormats() {
    return ecPointFormats;
  }

  public int[] getEllipticCurves() {
    return ellipticCurves;
  }

  public List<Diff> createDiff(Extensions ext) {
    List<Diff> diffs = new ArrayList<>();

    diffs.add(new Diff("ecPointFormats", ecPointFormats, ext.ecPointFormats));
    diffs.add(new Diff("ellipticCurves", ellipticCurves, ext.ellipticCurves));
    diffs.add(new Diff("heartbeat", heartbeat, ext.heartbeat));
    // diffs.add(new Diff("signatureAlgorithms", signatureAlgorithms, ext.signatureAlgorithms));

    return diffs;
  }

}
