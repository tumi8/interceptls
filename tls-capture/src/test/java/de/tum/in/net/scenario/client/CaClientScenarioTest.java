package de.tum.in.net.scenario.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;
import de.tum.in.net.server.FileTlsServerConfig;
import de.tum.in.net.server.MyResultListener;
import de.tum.in.net.util.CertificateUtil;

/**
 * Created by johannes on 31.05.17.
 */
public class CaClientScenarioTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private static Set<TrustAnchor> trustAnchors;

  @BeforeClass
  public static void initCerts() throws Exception {
    trustAnchors = new HashSet<>();
    X509Certificate ca = CertificateUtil.readCert(new File("certs", "ca-cert.pem"));
    trustAnchors.add(new TrustAnchor(ca, null));

  }

  @Test
  public void certPathInvalid() throws Exception {
    final int port = 3843;

    final MyResultListener listener = new MyResultListener();
    final ClientHandlerFactory fac =
        new DefaultClientHandlerFactory(new BcTlsServerFactory(), listener);
    try (final SimpleServerSocket socket = new SimpleServerSocket(port, fac, executor)) {
      executor.submit(socket);
      Thread.sleep(20);

      final CaClientScenario scenario = new CaClientScenario("127.0.0.1", port, trustAnchors);
      scenario.call();

      while (listener.result == null) {
        Thread.sleep(20);
      }
      assertFalse(listener.result.isSuccess());
    }

  }

  @Test
  public void certPathValid() throws Exception {
    final int port = 3843;

    final MyResultListener listener = new MyResultListener();
    BcTlsServerFactory tlsFac = new BcTlsServerFactory(
        new FileTlsServerConfig(new File("certs", "ca-cert.pem"), new File("certs", "ca-key.pem")));
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(tlsFac, listener);
    try (final SimpleServerSocket socket = new SimpleServerSocket(port, fac, executor)) {
      executor.submit(socket);
      Thread.sleep(20);

      final CaClientScenario scenario = new CaClientScenario("127.0.0.1", port, trustAnchors);
      final ScenarioResult clientResult = scenario.call();

      while (listener.result == null) {
        Thread.sleep(20);
      }
      assertTrue(listener.result.isSuccess());

      // the sent bytes must equal the received bytes
      assertArrayEquals(clientResult.getReceivedBytes(), listener.result.getSentBytes());
      assertArrayEquals(clientResult.getSentBytes(), listener.result.getReceivedBytes());
    }

  }
}
