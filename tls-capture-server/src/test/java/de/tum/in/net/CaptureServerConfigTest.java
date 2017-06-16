package de.tum.in.net;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsTestId;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

public class CaptureServerConfigTest {

  Properties props;

  @Before
  public void setUp() {
    props = new Properties();
    props.setProperty("test.session", "LOCAL");
    props.setProperty("target.url", "http://heise.de");
    props.setProperty("port", "443");
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyConfig() {
    Properties props = new Properties();
    new CaptureServerConfig(props);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyTestSession() {
    props.remove("test.session");
    new CaptureServerConfig(props);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyPort() {
    props.remove("port");
    new CaptureServerConfig(props);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyUrl() {
    props.remove("target.url");
    new CaptureServerConfig(props);
  }

  @Test
  public void okLocal() throws Exception {
    CaptureServerConfig conf = new CaptureServerConfig(props);
    TestSession session = conf.newTestSession();
    session.uploadHandshake(new ScenarioResultBuilder("src", "dst").error(new Throwable(),
        TlsTestId.randomID().getTestId()));
  }

  @Test
  public void okOnline() throws Exception {
    props.setProperty("test.session", "ONLINE");
    CaptureServerConfig conf = new CaptureServerConfig(props);
    TestSession session = conf.newTestSession();
    // session.uploadHandshake(new ScenarioResultBuilder("src", "dst").error(new Throwable(),
    // TlsTestId.randomID().getTestId()));
  }

}
