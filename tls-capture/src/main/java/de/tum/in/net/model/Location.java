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
