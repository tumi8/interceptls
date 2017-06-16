package de.tum.in.net.session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SessionIdTest {

  @Test
  public void equals() {
    SessionId s1 = new SessionId("s1");
    SessionId s1_copy = new SessionId("s1");
    SessionId s2 = new SessionId("s2");

    assertTrue(s1.equals(s1_copy));
    assertFalse(s1.equals(s2));
  }

}
