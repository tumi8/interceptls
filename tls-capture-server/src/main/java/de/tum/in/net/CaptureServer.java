package de.tum.in.net;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;
import de.tum.in.net.scenario.server.TlsServerConfig;
import de.tum.in.net.server.FileTlsServerConfig;

public class CaptureServer {


  private static final Logger log = LoggerFactory.getLogger(CaptureServer.class);
  private static final File CERT_FILE = new File("conf", "cert.pem");
  private static final File KEY_FILE = new File("conf", "key.pem");
  private static final File CONF_FILE = new File("conf", "server.properties");

  private final ExecutorService exec = Executors.newCachedThreadPool();
  private Thread serverThread;
  private SimpleServerSocket server;
  private final TlsServerFactory prov;

  private final ClientHandlerFactory handler;
  private CaptureServerConfig conf;

  public CaptureServer() throws IOException, CertificateException {
    this.conf = CaptureServerConfig.load(CONF_FILE);
    final TlsServerConfig config = new FileTlsServerConfig(CERT_FILE, KEY_FILE);
    prov = new BcTlsServerFactory(config);
    ResultListener<ScenarioResult> uploader = new ResultUploader(conf);
    handler = new DefaultClientHandlerFactory(prov, uploader);
  }

  public static void main(final String[] args) throws Exception {
    final CaptureServer srv = new CaptureServer();
    srv.start();
  }

  public synchronized void start() throws IllegalStateException {
    int port = conf.getPort();
    log.debug("Start CaptureServer on port {}", port);
    final SimpleServerSocket srvSocket = new SimpleServerSocket(port, handler, exec);
    server = srvSocket;
    serverThread = new Thread(srvSocket, "Server on port " + port);
    serverThread.start();
  }



  public boolean isRunning() {
    return serverThread.isAlive();
  }

  public synchronized void stop() throws IOException {
    server.close();
    exec.shutdown();
  }


}
