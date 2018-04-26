package de.tum.in.net.session;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLHandshakeException;

import org.junit.Test;

import de.tum.in.net.analysis.AnalysisAPI;
import de.tum.in.net.analysis.NetworkStats;
import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.NetworkType;
import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.FileTlsServerConfig;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.util.ServerUtil;
import retrofit2.Call;

public class APIClientTest {

  private int port = 52152;
  final ExecutorService exec = Executors.newCachedThreadPool();


  @Test
  public void clientConnectsWithKnownCert() throws Exception {
    BcTlsServerFactory tlsServerFac = new BcTlsServerFactory(
        new FileTlsServerConfig(new File("certs/analysis-server-cert-rsa.pem"),
            new File("certs/analysis-server-key-rsa.pem")));
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(tlsServerFac);

    try (final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec)) {
      exec.submit(srv);
      ServerUtil.waitForRunning(srv);

      NetworkId n = new NetworkId();
      n.setSsid("test");
      n.setType(NetworkType.WIFI);
      AnalysisAPI api =
          APIClient.createClient("https://localhost:" + port).create(AnalysisAPI.class);
      Call<NetworkStats> c = api.getNetworkStats(n);
      try {
        c.execute();
        fail();
      } catch (IOException e) {
        // expected due to incompatible api
        assertTrue(e.getCause() instanceof EOFException);
      }
    }
  }

  @Test(expected = SSLHandshakeException.class)
  public void clientAbortConnectionWithUnknownCert() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());

    try (final SimpleServerSocket srv = new SimpleServerSocket(port, fac, exec)) {
      exec.submit(srv);
      ServerUtil.waitForRunning(srv);

      NetworkId n = new NetworkId();
      n.setSsid("test");
      n.setType(NetworkType.WIFI);
      AnalysisAPI api =
          APIClient.createClient("https://localhost:" + port).create(AnalysisAPI.class);
      Call<NetworkStats> c = api.getNetworkStats(n);
      c.execute();
    }
  }

  @Test
  public void coverage() throws Exception {
    Constructor<APIClient> c = APIClient.class.getDeclaredConstructor();
    c.setAccessible(true);
    c.newInstance();
  }

}
