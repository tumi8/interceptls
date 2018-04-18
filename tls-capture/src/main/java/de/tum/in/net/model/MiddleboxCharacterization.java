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
  private final boolean usesSniToResolveHost;
  private final boolean usesHttpHostToResolveHost;

  private final List<ProtocolVersion> supportedTlsVersions;

  private MiddleboxCharacterization(boolean usesSniToResolveHost, boolean usesHttpHostToResolveHost,
      List<ProtocolVersion> supportedTlsVersions) {
    this.usesSniToResolveHost = usesSniToResolveHost;
    this.usesHttpHostToResolveHost = usesHttpHostToResolveHost;
    this.supportedTlsVersions = supportedTlsVersions;
  }

  public boolean getUsesSniToResolveHost() {
    return usesSniToResolveHost;
  }

  public boolean getUsesHttpHostToResolveHost() {
    return usesHttpHostToResolveHost;
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

    private boolean usesSniToResolveHost;
    private boolean usesHttpHostToResolveHost;
    private final List<ProtocolVersion> supportedTlsVersions = new ArrayList<>();

    public Builder setUsesSniToResolveHost(boolean usesSniToResolveHost) {
      this.usesSniToResolveHost = usesSniToResolveHost;
      return this;
    }

    public Builder setUsesHttpHostToResolveHost(boolean usesHttpHostToResolveHost) {
      this.usesHttpHostToResolveHost = usesHttpHostToResolveHost;
      return this;
    }

    public void setVersionSupport(ProtocolVersion version) {
      supportedTlsVersions.add(version);
    }

    public MiddleboxCharacterization build() {
      return new MiddleboxCharacterization(usesSniToResolveHost, usesHttpHostToResolveHost,
          supportedTlsVersions);
    }

  }
}
