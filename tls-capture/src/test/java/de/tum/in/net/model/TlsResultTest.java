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
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TlsResultTest {

  @Test
  public void simple() {
    TlsResult r = new TlsResult("destination.org", null, null);
    assertEquals("destination.org", r.getDestination());
    assertNull(r.getReceivedBytes());
    assertNull(r.getReceivedBytesRaw());
    assertNull(r.getSentBytes());
    assertNull(r.getSentBytesRaw());
  }


  @Test
  public void simple2() {
    String sent = "sentBytes";
    String received = "receivedBytes";

    TlsResult r = new TlsResult("destination.org", received.getBytes(), sent.getBytes());
    assertEquals("destination.org", r.getDestination());
    assertEquals(received, new String(r.getReceivedBytesRaw()));
    assertEquals(sent, new String(r.getSentBytesRaw()));
  }

  @Test
  public void printToString() {
    TlsResult r = new TlsResult("destination.org", null, null);
    assertEquals("TlsResult[destination.org]", r.toString());

  }

}
