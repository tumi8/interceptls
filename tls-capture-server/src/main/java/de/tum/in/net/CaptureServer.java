package de.tum.in.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.TlsConstants;
import de.tum.in.net.server.BcTlsServerFactory;
import de.tum.in.net.server.ClientHandlerFactory;
import de.tum.in.net.server.DefaultClientHandlerFactory;
import de.tum.in.net.server.FileTlsServerConfig;
import de.tum.in.net.server.SimpleServerSocket;
import de.tum.in.net.server.TlsServerConfig;
import de.tum.in.net.server.TlsServerFactory;

public class CaptureServer {


  private static final Logger log = LoggerFactory.getLogger(CaptureServer.class);
  private static final File CERT_FILE = new File("conf", "cert.pem");
  private static final File KEY_FILE = new File("conf", "key.pem");
  private static final File CONF_FILE = new File("conf", "server.properties");

  private static final String CERT_PROPERTY = "cert";
  private static final String KEY_PROPERTY = "key";
  private static final String PORT_PROPERTY = "port";
  private static final String REDIRECT_PROPERTY = "redirect";
  private static final String REDIRECT_URL_PROPERTY = "redirect_url";

  private final ExecutorService exec = Executors.newCachedThreadPool();
  private Thread serverThread;
  private SimpleServerSocket server;
  private final TlsServerFactory prov;

  private final ClientHandlerFactory handler;
  private final int port;

  public CaptureServer() throws CertificateException, IOException {
    this(CONF_FILE);
  }

  public CaptureServer(int port) throws CertificateException, IOException {
    this.port = port;
    final TlsServerConfig config = new FileTlsServerConfig(CERT_FILE, KEY_FILE);
    prov = new BcTlsServerFactory(config);
    handler = new DefaultClientHandlerFactory(prov);
  }

  public CaptureServer(File confFile) throws IOException, CertificateException {
    Properties props = new Properties();
    try (InputStream in = new FileInputStream(confFile)) {
      props.load(in);
      String portString = getNonEmptyProperty(props, PORT_PROPERTY);
      port = Integer.parseInt(portString);

      boolean redirect = Boolean.parseBoolean(getNonEmptyProperty(props, REDIRECT_PROPERTY));
      String redirectUrl = null;
      if (redirect) {
        redirectUrl = getOptionalProperty(props, REDIRECT_URL_PROPERTY);
        if (redirectUrl == null) {
          redirectUrl = TlsConstants.TLS_INFORMATION_SERVER_URL_WITH_PORT;
        }
      }
      String certPath = getOptionalProperty(props, CERT_PROPERTY);
      File certFile = certPath == null ? CERT_FILE : new File(certPath);

      String keyPath = getOptionalProperty(props, KEY_PROPERTY);
      File keyFile = keyPath == null ? KEY_FILE : new File(keyPath);

      final TlsServerConfig config = new FileTlsServerConfig(certFile, keyFile);
      prov = new BcTlsServerFactory(config);
      handler = new DefaultClientHandlerFactory(prov, redirectUrl);
    }

  }

  /**
   * @param props
   * @param key
   * @return null if the key does not exist or the value is empty.
   */
  private static String getOptionalProperty(Properties props, String key) {
    String value = props.getProperty(key);
    if (value != null) {
      value = value.trim();
    }
    return value == null || value.isEmpty() ? null : value;
  }

  private static String getNonEmptyProperty(Properties props, String key) {
    String value = props.getProperty(key);
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("The property " + key + " does not exist or is empty.");
    }
    return value.trim();
  }

  public static void main(final String[] args) throws Exception {
    final CaptureServer srv = new CaptureServer();
    srv.start();
  }

  public synchronized void start() throws IllegalStateException {
    log.debug("Start CaptureServer on port {}", port);
    server = new SimpleServerSocket(port, handler, exec);
    serverThread = new Thread(server, "Server on port " + port);
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
