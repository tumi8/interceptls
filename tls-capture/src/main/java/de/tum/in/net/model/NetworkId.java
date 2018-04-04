package de.tum.in.net.model;

import java.io.Serializable;

public class NetworkId implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -6305117111504240243L;

  private NetworkType type;
  private String publicIp;
  private String dnsIp;
  private String dnsMac;
  private String defaultGatewayIp;
  private String defaultGatewayMac;

  // wifi specific
  private String ssid;
  private String bssid;

  public void setSsid(String ssid) {
    this.ssid = ssid;
  }

  public String getSsid() {
    return this.ssid;
  }

  public String getBssid() {
    return bssid;
  }

  public void setBssid(String bssid) {
    this.bssid = bssid;
  }

  public String getDnsIp() {
    return dnsIp;
  }

  public void setDnsIp(String dnsIp) {
    this.dnsIp = dnsIp;
  }


  public String getDnsMac() {
    return dnsMac;
  }

  public void setDnsMac(String dnsMac) {
    this.dnsMac = dnsMac;
  }

  @Override
  public String toString() {
    return "NetworkId [ssid=" + ssid + ",bssid=" + bssid + ",defaultGatewayIp=" + defaultGatewayIp
        + ",defaultGatewayMac=" + defaultGatewayMac + ",dnsIp=" + dnsIp + ",dnsMac=" + dnsMac
        + ",publicIp=" + publicIp + "]";
  }

  public NetworkType getType() {
    return type;
  }

  public void setType(NetworkType type) {
    this.type = type;
  }

  public String getPublicIp() {
    return publicIp;
  }

  public void setPublicIp(String publicIp) {
    this.publicIp = publicIp;
  }

  public String getDefaultGatewayIp() {
    return defaultGatewayIp;
  }

  public void setDefaultGatewayIp(String defaultGatewayIp) {
    this.defaultGatewayIp = defaultGatewayIp;
  }

  public String getDefaultGatewayMac() {
    return defaultGatewayMac;
  }

  public void setDefaultGatewayMac(String defaultGatewayMac) {
    this.defaultGatewayMac = defaultGatewayMac;
  }

}
