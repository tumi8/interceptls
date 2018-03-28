package de.tum.in.net.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

/**
 * Created by johannes on 31.03.17.
 */
public class DefaultProxyScenarioTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();

  @Test
  public void testOK() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      Thread.sleep(10);

      HostAndPort target = new HostAndPort("127.0.0.1", socket.getLocalPort());
      final DefaultProxyScenario scenario = new DefaultProxyScenario(target);

      final TlsClientServerResult result = scenario.call();
      TlsResult clientResult = result.getClientResult();

      assertTrue(result.isSuccess());
      assertNull(result.getServerResult());

      assertNotNull(clientResult.getReceivedBytes());


    }

  }
}
