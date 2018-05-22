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
package de.tum.in.net.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.tls.ProtocolVersion;

public class MiddleboxCharacterization implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -8888352681650722935L;
  private final boolean canConnectWrongSni;
  private final boolean canConnectWrongHttpHost;

  private final List<ProtocolVersion> supportedTlsVersions;

  private MiddleboxCharacterization(boolean canConnectWrongSni, boolean canConnectWrongHttpHost,
      List<ProtocolVersion> supportedTlsVersions) {
    this.canConnectWrongSni = canConnectWrongSni;
    this.canConnectWrongHttpHost = canConnectWrongHttpHost;
    this.supportedTlsVersions = supportedTlsVersions;
  }

  public boolean getCanConnectWrongSni() {
    return canConnectWrongSni;
  }

  public boolean getCanConnectWrongHttpHost() {
    return canConnectWrongHttpHost;
  }

  public List<ProtocolVersion> getSupportedTlsVersions() {
    return Collections.unmodifiableList(this.supportedTlsVersions);
  }

  public boolean isSslV3() {
    return supportedTlsVersions.contains(ProtocolVersion.SSLv3);
  }

  public boolean isTlsV10() {
    return supportedTlsVersions.contains(ProtocolVersion.TLSv10);
  }

  public boolean isTlsV11() {
    return supportedTlsVersions.contains(ProtocolVersion.TLSv11);
  }

  public boolean isTlsV12() {
    return supportedTlsVersions.contains(ProtocolVersion.TLSv12);
  }


  public static class Builder {

    private boolean canConnectWrongSni;
    private boolean canConnectWrongHttpHost;
    private final List<ProtocolVersion> supportedTlsVersions = new ArrayList<>();

    public Builder setCanConnectWrongSni(boolean canConnectWrongSni) {
      this.canConnectWrongSni = canConnectWrongSni;
      return this;
    }

    public Builder setCanConnectWrongHttpHost(boolean canConnectWrongHttpHost) {
      this.canConnectWrongHttpHost = canConnectWrongHttpHost;
      return this;
    }

    public void setVersionSupport(ProtocolVersion version) {
      supportedTlsVersions.add(version);
    }

    public MiddleboxCharacterization build() {
      return new MiddleboxCharacterization(canConnectWrongSni, canConnectWrongHttpHost,
          supportedTlsVersions);
    }

  }
}
