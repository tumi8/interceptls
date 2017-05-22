package de.tum.in.net.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.scenario.client.DefaultClientScenario;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;

/**
 * Created by johannes on 17.05.17.
 */

public class SimpleServerSocketTest {

  private final int port = 34234;
  final ExecutorService exec = Executors.newSingleThreadExecutor();
  final ClientHandlerFactory fac =
      new DefaultClientHandlerFactory(new BcTlsServerFactory(), new MyResultListener());

  @Test(expected = IllegalStateException.class)
  public void cannotStopIfNotStarted() throws Exception {
    final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec);
    srv.close();
  }

  @Test
  public void canStartAndStop() throws Exception {
    final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec);
    assertFalse(srv.isRunning());

    final Thread srvThread = new Thread(srv);
    srvThread.start();
    Thread.sleep(10);

    assertTrue(srv.isRunning());

    srv.close();
    assertFalse(srv.isRunning());
  }

  @Test
  public void tlsClientServerTest() throws Exception {
    final ExecutorService exec = Executors.newCachedThreadPool();
    final MyResultListener publisher = new MyResultListener();
    final ClientHandlerFactory fac =
        new DefaultClientHandlerFactory(new BcTlsServerFactory(), publisher);

    try (final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec)) {
      final Thread t = new Thread(srv);
      t.start();
      Thread.sleep(30);

      final Thread clientThread = new Thread(new Runnable() {
        @Override
        public void run() {
          new DefaultClientScenario("127.0.0.1", port).call();
        }
      });
      clientThread.start();

      while (publisher.result == null) {
        Thread.sleep(20);
      }

      assertNotNull(publisher.result);
      assertTrue(publisher.result.isSuccess());
    }


  }
}
