package de.tum.in.net.session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SessionIdTest {

  @Test
  public void equals() {
    SessionID s1 = new SessionID("s1");
    SessionID s1_copy = new SessionID("s1");
    SessionID s2 = new SessionID("s2");

    assertTrue(s1.equals(s1_copy));
    assertFalse(s1.equals(s2));
  }

}
