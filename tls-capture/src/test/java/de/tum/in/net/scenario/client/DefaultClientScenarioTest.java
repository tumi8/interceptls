package de.tum.in.net.scenario.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.TlsTestId;
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
    final int port = 3843;
    String sessionId = "sessionId";
    String testId = "testId";
    TlsTestId id = new TlsTestId(sessionId, testId);

    final MyResultListener listener = new MyResultListener();
    final ClientHandlerFactory fac =
        new DefaultClientHandlerFactory(new BcTlsServerFactory(), listener);
    try (final SimpleServerSocket socket = new SimpleServerSocket(port, fac, executor)) {
      executor.submit(socket);
      Thread.sleep(20);

      final DefaultClientScenario scenario = new DefaultClientScenario(id, "127.0.0.1", port);

      final ScenarioResult clientResult = scenario.call();

      while (listener.result == null) {
        Thread.sleep(20);
      }
      assertTrue(listener.result.isSuccess());

      // the sent bytes must equal the received bytes
      assertArrayEquals(clientResult.getReceivedBytes(), listener.result.getSentBytes());
      assertArrayEquals(clientResult.getSentBytes(), listener.result.getReceivedBytes());

      // received test id's
      assertEquals(sessionId, listener.id.getID());
      assertEquals(testId, listener.result.getTestId().toString());
    }

  }
}
