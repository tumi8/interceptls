package de.tum.in.net.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.util.ServerUtil;

/**
 * Created by johannes on 31.03.17.
 */
public class FakeHostHttpsScenarioTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Test
  public void testOK() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      ServerUtil.waitForRunning(socket);

      HostAndPort target = new HostAndPort("127.0.0.1", socket.getLocalPort());
      final FakeHostHttpsScenario scenario =
          new FakeHostHttpsScenario(target, "not.existant." + target.getHost());

      final TlsClientServerResult result = scenario.call();
      TlsResult clientResult = result.getClientResult();
      TlsResult serverResult = result.getServerResult();


      assertTrue(result.isSuccess());

      // the sent bytes must equal the received bytes
      assertArrayEquals(clientResult.getReceivedBytesRaw(), serverResult.getSentBytesRaw());
      assertArrayEquals(clientResult.getSentBytesRaw(), serverResult.getReceivedBytesRaw());

    }

  }

}