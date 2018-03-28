package de.tum.in.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.tum.in.net.client.DefaultHttpsScenario;
import de.tum.in.net.client.HostAndPort;
import de.tum.in.net.model.TlsClientServerResult;

/**
 * Created by johannes on 16.05.17.
 */
public class CaptureServerTest {

  private final int port = 34234;

  @Test
  public void startAndStop() throws Exception {
    final CaptureServer server = new CaptureServer(port);

    server.start();
    assertTrue(server.isRunning());
    Thread.sleep(20);

    server.stop();
    Thread.sleep(20);
    assertFalse(server.isRunning());
  }

  @Test
  public void acceptClient() throws Exception {
    final CaptureServer server = new CaptureServer(port);

    server.start();
    assertTrue(server.isRunning());
    Thread.sleep(20);

    HostAndPort target = new HostAndPort("127.0.0.1", port);
    DefaultHttpsScenario client = new DefaultHttpsScenario(target);
    TlsClientServerResult result = client.call();
    assertTrue(result.isSuccess());

    server.stop();
    Thread.sleep(20);
    assertFalse(server.isRunning());
  }

  @Test
  public void multiThreading() throws Exception {
    int numberOfClients = 50;
    final CaptureServer server = new CaptureServer(port);

    server.start();
    assertTrue(server.isRunning());
    Thread.sleep(20);

    ExecutorService exec = Executors.newFixedThreadPool(numberOfClients / 2);
    List<Callable<TlsClientServerResult>> clients = new ArrayList<>();
    for (int i = 0; i < numberOfClients; i++) {
      HostAndPort target = new HostAndPort("127.0.0.1", port);
      clients.add(new DefaultHttpsScenario(target));
    }

    List<Future<TlsClientServerResult>> results = exec.invokeAll(clients, 5, TimeUnit.SECONDS);
    for (Future<TlsClientServerResult> result : results) {
      assertFalse(result.isCancelled());
      assertTrue(result.get().isSuccess());
    }

    server.stop();
    Thread.sleep(20);
    assertFalse(server.isRunning());
  }

}
