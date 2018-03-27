package de.tum.in.net.analysis;

public class SNI {

  short type;
  byte[] name;

  @Override
  public String toString() {
    return type + " - " + new String(name);
  }
}
