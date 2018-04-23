package de.tum.in.net.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bouncycastle.tls.ProtocolVersion;
import org.junit.Test;

public class MiddleboxCharacterizationTest {

  @Test
  public void simple() {
    MiddleboxCharacterization.Builder b = new MiddleboxCharacterization.Builder();

    b.setCanConnectWrongHttpHost(true);
    b.setCanConnectWrongSni(false);
    b.setVersionSupport(ProtocolVersion.TLSv10);
    b.setVersionSupport(ProtocolVersion.TLSv11);

    MiddleboxCharacterization c = b.build();

    assertTrue(c.getCanConnectWrongHttpHost());
    assertFalse(c.getCanConnectWrongSni());

    assertFalse(c.isSslV3());
    assertTrue(c.isTlsV10());
    assertTrue(c.isTlsV11());
    assertFalse(c.isTlsV12());
    assertEquals("[TLS 1.0, TLS 1.1]", c.getSupportedTlsVersions().toString());
  }
}
