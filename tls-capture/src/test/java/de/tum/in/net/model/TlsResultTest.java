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
