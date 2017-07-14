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

import org.junit.Before;
import org.junit.Test;

import de.tum.in.net.CaptureServerConfig.TestSessionType;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.client.DefaultClientScenario;

/**
 * Created by johannes on 16.05.17.
 */
public class CaptureServerTest {

  private CaptureServerConfig conf;
  private final int port = 34234;
  private final TestID id = TestID.randomID();

  @Before
  public void setUp() {
    conf = new CaptureServerConfig("url", TestSessionType.LOCAL, port);
  }

  @Test
  public void startAndStop() throws Exception {
    final CaptureServer server = new CaptureServer(conf);

    server.start();
    assertTrue(server.isRunning());
    Thread.sleep(10);

    server.stop();
    Thread.sleep(50);
    assertFalse(server.isRunning());
  }

  @Test
  public void acceptClient() throws Exception {
    final CaptureServer server = new CaptureServer(conf);

    server.start();
    assertTrue(server.isRunning());
    Thread.sleep(10);

    DefaultClientScenario client = new DefaultClientScenario(id, "127.0.0.1", port);
    ScenarioResult result = client.call();
    assertTrue(result.isSuccess());

    server.stop();
    Thread.sleep(50);
    assertFalse(server.isRunning());
  }

  @Test
  public void multiThreading() throws Exception {
    int numberOfClients = 50;
    final CaptureServer server = new CaptureServer(conf);

    server.start();
    assertTrue(server.isRunning());
    Thread.sleep(20);

    ExecutorService exec = Executors.newFixedThreadPool(numberOfClients / 2);
    List<Callable<ScenarioResult>> clients = new ArrayList<>();
    for (int i = 0; i < numberOfClients; i++) {
      clients.add(new DefaultClientScenario(id, "127.0.0.1", port));
    }

    List<Future<ScenarioResult>> results = exec.invokeAll(clients, 5, TimeUnit.SECONDS);
    for (Future<ScenarioResult> result : results) {
      assertFalse(result.isCancelled());
      assertTrue(result.get().isSuccess());
    }


    server.stop();
    Thread.sleep(50);
    assertFalse(server.isRunning());
  }

}
