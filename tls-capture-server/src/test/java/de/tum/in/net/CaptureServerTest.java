package de.tum.in.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Created by johannes on 16.05.17.
 */
public class CaptureServerTest {

  @Test
  public void startAndStop() throws Exception {
    final CaptureServer server = new CaptureServer();

    server.start();
    Thread.sleep(50);
    assertTrue(server.isRunning());

    server.stop();
    Thread.sleep(50);
    assertFalse(server.isRunning());
  }

}
