package de.tum.in.net;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;
import de.tum.in.net.scenario.server.TlsServerConfig;
import de.tum.in.net.server.FileTlsServerConfig;
import de.tum.in.net.session.LoggingTestSession;

public class CaptureServer {


  private static final Logger log = LoggerFactory.getLogger(CaptureServer.class);
  private static final File CERT_FILE = new File("conf", "cert.pem");
  private static final File KEY_FILE = new File("conf", "key.pem");

  private final ExecutorService exec = Executors.newCachedThreadPool();
  private final Thread[] serverThreads;
  private final int[] ports;
  private final SimpleServerSocket[] server;
  private final TlsServerFactory prov;

  private final ClientHandlerFactory handler;

  public CaptureServer(final int... ports) throws IOException, CertificateException {
    this.ports = Objects.requireNonNull(ports, "ports must not be null");
    server = new SimpleServerSocket[ports.length];
    serverThreads = new Thread[ports.length];
    final TlsServerConfig config = new FileTlsServerConfig(CERT_FILE, KEY_FILE);
    prov = new BcTlsServerFactory(config);
    handler = new DefaultClientHandlerFactory(prov, (result) -> {
      try {
        // TODO define ip
        // FixedIdTestSession session = new FixedIdTestSession("TODO");
        LoggingTestSession session = new LoggingTestSession();
        session.uploadHandshake(Arrays.asList(result));
      } catch (IOException e) {
        log.error("Could not upload result to analysis-server.", e);
      }

    });
  }

  public static void main(final String[] args) throws Exception {
    final CaptureServer srv = new CaptureServer(7364);
    srv.start();
  }

  public synchronized void start() throws IllegalStateException {
    log.debug("Start CaptureServer on ports {}", Arrays.toString(ports));
    for (int i = 0; i < ports.length; i++) {
      final SimpleServerSocket srvSocket = new SimpleServerSocket(ports[i], handler, exec);
      server[i] = srvSocket;
      serverThreads[i] = new Thread(srvSocket, "Server Port " + ports[i]);
      serverThreads[i].start();
    }
  }

  public boolean isRunning() {
    for (final Thread t : serverThreads) {
      if (t.isAlive()) {
        return true;
      }
    }
    return false;
  }

  public synchronized void stop() throws IOException {
    for (final SimpleServerSocket t : server) {
      t.close();
    }
    exec.shutdown();
  }


}
