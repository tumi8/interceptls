package de.tum.in.net.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkId implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -6305117111504240243L;

  private NetworkType type;
  private String publicIp;
  private List<IpAndMac> dns = new ArrayList<>();
  private String defaultGatewayIp;
  private String defaultGatewayMac;

  // optional
  private Location location;

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

  public List<IpAndMac> getDns() {
    return Collections.unmodifiableList(dns);
  }

  public void addDns(IpAndMac dns) {
    this.dns.add(dns);
  }

  @Override
  public String toString() {
    return "NetworkId [ssid=" + ssid + ",bssid=" + bssid + ",defaultGatewayIp=" + defaultGatewayIp
        + ",defaultGatewayMac=" + defaultGatewayMac + ",dns=" + dns + ",publicIp=" + publicIp
        + ",location=" + location + "]";
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

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

}
