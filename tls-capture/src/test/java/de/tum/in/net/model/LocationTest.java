package de.tum.in.net.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LocationTest {

  @Test
  public void storesOnlyTwoDecimalPlaces() {
    Location l = new Location(23.1234, 12.3456);
    assertEquals(23.12, l.getLongitude(), 0);
    assertEquals(12.34, l.getLatitude(), 0);

    l = new Location(23.1, 12.3);
    assertEquals(23.1, l.getLongitude(), 0);
    assertEquals(12.3, l.getLatitude(), 0);


    l = new Location(-23.1234, -12.3456);
    assertEquals(-23.12, l.getLongitude(), 0);
    assertEquals(-12.34, l.getLatitude(), 0);
  }
}
