package de.tum.in.net.model;

public class Location {

  private final double longitude;
  private final double latitude;


  // due to privacy we limit the number of decimal places to two
  public Location(double longitude, double latitude) {
    this.longitude = limitPrecision(longitude);
    this.latitude = limitPrecision(latitude);
  }

  private final double limitPrecision(double value) {
    int val = (int) (value * 100.0);
    return ((double) val) / 100.0;
  }


  public double getLongitude() {
    return longitude;
  }


  public double getLatitude() {
    return latitude;
  }

  @Override
  public String toString() {
    return "longitude:" + longitude + ", latitude:" + latitude;
  }
}
