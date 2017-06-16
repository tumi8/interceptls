package de.tum.in.net.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import de.tum.in.net.session.SessionId;

/**
 * Test is defined as <testSessionId>-<testId>
 * 
 * @author johannes
 *
 */
public class TlsTestId {

  private SessionId testSessionId;
  private TestId testId;

  public TlsTestId(String testSessionId, String testId) {
    this(new SessionId(testSessionId), new TestId(testId));
  }

  public TlsTestId(SessionId testSessionId, TestId testId) {
    this.testSessionId = testSessionId;
    this.testId = testId;
  }

  public SessionId getTestSessionId() {
    return testSessionId;
  }

  public TestId getTestId() {
    return testId;
  }

  @Override
  public String toString() {
    return testSessionId.getID() + "-" + testId.getID();
  }

  public static TlsTestId read(InputStream in) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      String str = reader.readLine();
      System.err.println(str);
      String[] splitted = str.split("-");
      System.err.println(Arrays.toString(splitted));
      return new TlsTestId(splitted[0], splitted[1]);
    }

  }

  public byte[] getTransmitBytes() {
    return toString().getBytes();
  }

  public static TlsTestId randomID() {
    return new TlsTestId(new BigInteger(130, new SecureRandom()).toString(32),
        new BigInteger(130, new SecureRandom()).toString(32));
  }



}
