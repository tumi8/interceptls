package de.tum.in.net.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

import de.tum.in.net.session.SessionID;

/**
 * Test is defined as <testSessionId>-<testId>
 * 
 * @author johannes
 *
 */
public class TestID {

  private SessionID sessionID;
  private int counter;

  public TestID(String sessionID, int counter) {
    this(new SessionID(sessionID), counter);
  }

  public TestID(SessionID sessionID, int counter) {
    this.sessionID = sessionID;
    this.counter = counter;
  }

  public SessionID getSessionID() {
    return sessionID;
  }

  public int getCounter() {
    return counter;
  }

  @Override
  public String toString() {
    return sessionID + "-" + counter;
  }

  public static TestID read(InputStream in) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      String str = reader.readLine();
      return parse(str);
    }

  }

  public byte[] getTransmitBytes() {
    return toString().getBytes();
  }

  public static TestID randomID() {
    return new TestID(new BigInteger(130, new SecureRandom()).toString(32),
        new BigInteger(8, new SecureRandom()).intValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(sessionID, counter);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof TestID) {
      TestID other = (TestID) o;
      return this.sessionID.equals(other.sessionID) && this.counter == other.counter;
    }
    return false;

  }

  public static TestID parse(String str) {
    Objects.requireNonNull(str, "str must not be null");
    String[] splitted = str.split("-");
    if (splitted.length != 2) {
      throw new IllegalArgumentException("TestID must be <SessionID>-<counter>");
    }
    return new TestID(splitted[0], Integer.parseInt(splitted[1]));
  }


}
