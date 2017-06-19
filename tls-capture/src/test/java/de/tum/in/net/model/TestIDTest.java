package de.tum.in.net.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestIDTest {

  @Test
  public void simple() {
    TestID id = new TestID("bla", 4);
    assertEquals("bla", id.getSessionID().toString());
    assertEquals(4, id.getCounter());
  }

  @Test
  public void equals() {
    TestID id = new TestID("bla", 4);
    TestID id_copy = new TestID("bla", 4);

    TestID idFoo = new TestID("foo", 4);
    TestID idCounter = new TestID("bla", 5);

    assertTrue(id.equals(id_copy));

    assertFalse(id.equals(idFoo));
    assertFalse(id.equals(idCounter));

  }

  @Test
  public void parse() {
    TestID parsed = TestID.parse("8293u-45");
    assertEquals("8293u", parsed.getSessionID().toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse_error() {
    TestID.parse("8293u-45-45");
  }

  @Test(expected = IllegalArgumentException.class)
  public void parse_noNumber() {
    TestID.parse("8293u-abc");
  }

  @Test
  public void isTestIDTest() {
    assertTrue(TestID.isTestID("jieFIEW38234-234"));
    assertTrue(TestID.isTestID("jieFIEW38234-234"));
    assertTrue(TestID.isTestID("a-1"));

    assertFalse(TestID.isTestID("jieFIEW38234-abc"));
    assertFalse(TestID.isTestID("jieFIEW38234-"));
    assertFalse(TestID.isTestID("-234"));
    assertFalse(TestID.isTestID("-"));

  }

}
