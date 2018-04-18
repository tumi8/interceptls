package de.tum.in.net.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.tls.ProtocolVersion;

public class MiddleboxCharacterization implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -8888352681650722935L;
  private final boolean usesSniToResolveHost;
  private final boolean usesHttpHostToResolveHost;
  private final Map<ProtocolVersion, Boolean> supportedTlsVersion;


  private MiddleboxCharacterization(boolean usesSniToResolveHost, boolean usesHttpHostToResolveHost,
      Map<ProtocolVersion, Boolean> supportedTlsVersion) {
    this.usesSniToResolveHost = usesSniToResolveHost;
    this.usesHttpHostToResolveHost = usesHttpHostToResolveHost;
    this.supportedTlsVersion = supportedTlsVersion;
  }

  public boolean getUsesSniToResolveHost() {
    return usesSniToResolveHost;
  }

  public boolean getUsesHttpHostToResolveHost() {
    return usesHttpHostToResolveHost;
  }

  public Map<ProtocolVersion, Boolean> getSupportedTlsVersion() {
    return supportedTlsVersion;
  }


  public static class Builder {

    private boolean usesSniToResolveHost;
    private boolean usesHttpHostToResolveHost;
    private final Map<ProtocolVersion, Boolean> supportedTlsVersion = new HashMap<>();

    public Builder setUsesSniToResolveHost(boolean usesSniToResolveHost) {
      this.usesSniToResolveHost = usesSniToResolveHost;
      return this;
    }

    public Builder setUsesHttpHostToResolveHost(boolean usesHttpHostToResolveHost) {
      this.usesHttpHostToResolveHost = usesHttpHostToResolveHost;
      return this;
    }

    public MiddleboxCharacterization build() {
      return new MiddleboxCharacterization(usesSniToResolveHost, usesHttpHostToResolveHost,
          supportedTlsVersion);
    }

    public void setSupportTlsVersion(ProtocolVersion version, boolean success) {
      supportedTlsVersion.put(version, success);
    }


  }
}
