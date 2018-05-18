/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.analysis;

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

  public List<SNI> getSni() {
    return sni;
  }

  public Map<String, Diff> createDiff(Extensions ext) {
    Map<String, Diff> diffs = new HashMap<>();

    if (ecPointFormats != null && ext.ecPointFormats != null) {
      diffs.put("ecPointFormats", new Diff(ecPointFormats, ext.ecPointFormats));
    }
    if (ellipticCurves != null && ext.ellipticCurves != null) {
      diffs.put("ellipticCurves", new Diff(ellipticCurves, ext.ellipticCurves));
    }
    if (heartbeat != null && ext.heartbeat != null) {
      diffs.put("heartbeat", new Diff(heartbeat, ext.heartbeat));
    }
    if (signatureAlgorithms != null && ext.signatureAlgorithms != null) {
      diffs.put("signatureAlgorithms", new Diff(signatureAlgorithms, ext.signatureAlgorithms));
    }
    if (sni != null && ext.sni != null) {
      diffs.put("sni", new Diff(sni, ext.sni));
    }

    return diffs;
  }

}
