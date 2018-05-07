package de.tum.in.net.model;

import java.util.Objects;

public class IpAndMac {

  // required
  private String ip;

  // optional
  private String mac;

  public IpAndMac(String ip, String mac) {
    this.ip = Objects.requireNonNull(ip);
    this.mac = mac;
  }

  public String getIp() {
    return ip;
  }

  public String getMac() {
    return mac;
  }

  @Override
  public String toString() {
    return ip + (mac == null ? "" : ("(" + mac + ")"));
  }

}
