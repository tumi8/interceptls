package de.tum.in.net.scenario.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;
import de.tum.in.net.server.MyResultListener;

/**
 * Created by johannes on 31.03.17.
 */
public class DefaultClientScenarioTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Test
  public void testOK() throws Exception {
    String sessionId = "sessionId";
    int counter = 6;
    TestID testID = new TestID(sessionId, counter);

    final MyResultListener listener = new MyResultListener();
    final ClientHandlerFactory fac =
        new DefaultClientHandlerFactory(new BcTlsServerFactory(), listener);
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      Thread.sleep(20);

      final DefaultClientScenario scenario =
          new DefaultClientScenario(testID, "127.0.0.1", socket.getLocalPort());

      final ScenarioResult clientResult = scenario.call();

      while (listener.result == null) {
        Thread.sleep(20);
      }
      assertTrue(listener.result.isSuccess());

      // the sent bytes must equal the received bytes
      assertArrayEquals(clientResult.getReceivedBytesRaw(), listener.result.getSentBytesRaw());
      assertArrayEquals(clientResult.getSentBytesRaw(), listener.result.getReceivedBytesRaw());

      // received test id's
      assertEquals(testID, listener.testID);
    }

  }
}
