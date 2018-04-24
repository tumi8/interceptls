package de.tum.in.net.client;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bouncycastle.tls.ProtocolVersion;
import org.bouncycastle.tls.TlsClientProtocol;
import org.bouncycastle.tls.TlsFatalAlert;
import org.junit.Test;

import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.util.ServerUtil;

public class VersionedTlsClientTest {

  private final ExecutorService executor = Executors.newCachedThreadPool();


  @Test
  public void canConnectDifferentVersions() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      ServerUtil.waitForRunning(socket);

      for (ProtocolVersion v : new ProtocolVersion[] {ProtocolVersion.TLSv10,
          ProtocolVersion.TLSv11, ProtocolVersion.TLSv12}) {
        VersionedTlsClient c = new VersionedTlsClient("sni", v);
        assertEquals(v, c.getClientVersion());
        assertEquals(v, c.getMinimumVersion());
        TlsClientProtocol p = new TlsClientProtocol();
        p.connect(c);

        p.close();
      }

    }
  }


  @Test(expected = TlsFatalAlert.class)
  public void cannotConnectOldVersion() throws Exception {
    final ClientHandlerFactory fac = new DefaultClientHandlerFactory(new BcTlsServerFactory());
    try (final SimpleServerSocket socket = new SimpleServerSocket(0, fac, executor)) {
      executor.submit(socket);
      ServerUtil.waitForRunning(socket);


      ProtocolVersion v = ProtocolVersion.SSLv3;
      VersionedTlsClient c = new VersionedTlsClient("sni", v);
      assertEquals(v, c.getClientVersion());
      assertEquals(v, c.getMinimumVersion());
      TlsClientProtocol p = new TlsClientProtocol();
      p.connect(c);

    }
  }

}
