package de.tum.in.net.model;

import java.io.Serializable;

public class MiddleboxCharacterization implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -8888352681650722935L;
  private final boolean usesSniToResolveHost;
  private final boolean usesHttpHostToResolveHost;


  private MiddleboxCharacterization(boolean usesSniToResolveHost,
      boolean usesHttpHostToResolveHost) {
    this.usesSniToResolveHost = usesSniToResolveHost;
    this.usesHttpHostToResolveHost = usesHttpHostToResolveHost;
  }

  public boolean getUsesSniToResolveHost() {
    return usesSniToResolveHost;
  }

  public boolean getUsesHttpHostToResolveHost() {
    return usesHttpHostToResolveHost;
  }


  public static class Builder {

    private boolean usesSniToResolveHost;
    private boolean usesHttpHostToResolveHost;

    public Builder setUsesSniToResolveHost(boolean usesSniToResolveHost) {
      this.usesSniToResolveHost = usesSniToResolveHost;
      return this;
    }

    public Builder setUsesHttpHostToResolveHost(boolean usesHttpHostToResolveHost) {
      this.usesHttpHostToResolveHost = usesHttpHostToResolveHost;
      return this;
    }

    public MiddleboxCharacterization build() {
      return new MiddleboxCharacterization(usesSniToResolveHost, usesHttpHostToResolveHost);
    }


  }
}
