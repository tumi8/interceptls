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
