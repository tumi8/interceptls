package de.tum.in.net.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bouncycastle.tls.ProtocolVersion;
import org.junit.Test;

public class MiddleboxCharacterizationTest {

  @Test
  public void simple() {
    MiddleboxCharacterization.Builder b = new MiddleboxCharacterization.Builder();

    b.setUsesHttpHostToResolveHost(true);
    b.setUsesSniToResolveHost(false);
    b.setVersionSupport(ProtocolVersion.TLSv10);
    b.setVersionSupport(ProtocolVersion.TLSv11);

    MiddleboxCharacterization c = b.build();

    assertTrue(c.getUsesHttpHostToResolveHost());
    assertFalse(c.getUsesSniToResolveHost());

    assertFalse(c.isSslV3());
    assertTrue(c.isTlsV10());
    assertTrue(c.isTlsV11());
    assertFalse(c.isTlsV12());
  }
}
