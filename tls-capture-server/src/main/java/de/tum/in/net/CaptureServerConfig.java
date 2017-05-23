package de.tum.in.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.session.FixedIdTestSession;
import de.tum.in.net.session.LoggingTestSession;

public class CaptureServerConfig {

  private enum TestSessionType {
    ONLINE, LOCAL
  }

  private static final String TARGET_URL = "target.url";
  private static final String TEST_SESSION = "test.session";
  private static final String PORT = "port";

  private final String target_url;
  private final TestSessionType test_session;
  private final int port;

  public CaptureServerConfig(Properties props) {
    this.target_url = getNonEmptyProperty(props, TARGET_URL);
    this.test_session = TestSessionType.valueOf(getNonEmptyProperty(props, TEST_SESSION));

    this.port = Integer.parseInt(getNonEmptyProperty(props, PORT));
  }

  public String getTargetUrl() {
    return this.target_url;
  }

  public int getPort() {
    return this.port;
  }

  public static CaptureServerConfig load(File confFile) throws IOException {
    Properties props = new Properties();
    try (InputStream in = new FileInputStream(confFile)) {
      props.load(in);
    }
    return new CaptureServerConfig(props);
  }

  public TestSession getNewTestSession() throws IOException {
    switch (this.test_session) {
      case LOCAL:
        return new LoggingTestSession();
      case ONLINE:
        return new FixedIdTestSession(target_url);
      default:
        throw new IllegalStateException("unknown test session: " + this.test_session);
    }

  }

  private static String getNonEmptyProperty(Properties props, String key) {
    String value = props.getProperty(key);
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("The property " + key + " does not exist or is empty.");
    }
    return value.trim();
  }

}
