package de.tum.in.net.client;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.Security;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.FileTlsServerConfig;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.util.CertificateUtil;
import de.tum.in.net.util.ServerUtil;

/**
 * Created by johannes on 31.05.17.
 */
public class CaClientScenarioTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();
  private static Set<TrustAnchor> trustAnchors;

  @BeforeClass
  public static void initCerts() throws Exception {
    Security.insertProviderAt(new BouncyCastleProvider(), 1);
    trustAnchors = new HashSet<>();
    X509Certificate ca = CertificateUtil.readCert(new File("certs", "ca-cert.pem"));
    trustAnchors.add(new TrustAnchor(ca, null));

  }

  @Test
  public void certPathInvalid() throws Exception {

    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      ServerUtil.waitForRunning(socket);

      HostAndPort target = new HostAndPort("127.0.0.1", socket.getLocalPort());
      final CaClientScenario scenario = new CaClientScenario(target, trustAnchors);
      TlsClientServerResult result = scenario.call();

      assertFalse(result.isSuccess());
      assertNull(result.getServerResult());

    }

  }

  @Test
  public void certPathValid() throws Exception {
    BcTlsServerFactory tlsFac = new BcTlsServerFactory(
        new FileTlsServerConfig(new File("certs", "ca-cert.pem"), new File("certs", "ca-key.pem")));
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(tlsFac);
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      ServerUtil.waitForRunning(socket);

      HostAndPort target = new HostAndPort("127.0.0.1", socket.getLocalPort());
      final CaClientScenario scenario = new CaClientScenario(target, trustAnchors);
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
