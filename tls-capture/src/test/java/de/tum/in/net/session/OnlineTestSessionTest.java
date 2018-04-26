package de.tum.in.net.session;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class OnlineTestSessionTest {

  @Test(expected = ConnectException.class)
  public void noConnection() throws Exception {
    TestSession s = new OnlineTestSession("https://127.0.0.1:64523");
    List<TlsClientServerResult> results = Arrays.asList();
    TlsTestResult r = new TlsTestResult(new NetworkId(), results);
    s.uploadResult(r);
  }

  @Test(expected = IllegalArgumentException.class)
  public void cannotUploadNull() throws Exception {
    TestSession s = new OnlineTestSession("https://127.0.0.1:3000");
    s.uploadResult(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalUrl() {
    new OnlineTestSession("bla bla");
  }
}
