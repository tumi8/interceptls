package de.tum.in.net;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by johannes on 16.05.17.
 */
public class CaptureServerTest {

  @Test
  public void startAndStop() throws Exception {
    final CaptureServer server = new CaptureServer(8989);

    server.start();
    Thread.sleep(50);
    assertTrue(server.isRunning());

    server.stop();
    Thread.sleep(50);
    assertFalse(server.isRunning());
  }

}
