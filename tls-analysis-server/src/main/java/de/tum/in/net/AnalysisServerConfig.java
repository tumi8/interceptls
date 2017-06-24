package de.tum.in.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

public class AnalysisServerConfig {

  private static final File CONF_FILE = new File("conf", "server.properties");
  private static final String PORT = "port";
  private static final String TLS_KEYSTORE_FILE = "tls.keystore.file";
  private static final String TLS_KEYSTORE_PASSWORD = "tls.keystore.password";

  private final int port;
  private final String keystorePassword;

  public AnalysisServerConfig(int port, String keystorePassword) {
    this.port = port;
    this.keystorePassword = keystorePassword;
  }

  public AnalysisServerConfig(Properties props) {
    this.port = Integer.parseInt(getNonEmptyProperty(props, PORT));
    this.keystorePassword = getNonEmptyProperty(props, TLS_KEYSTORE_PASSWORD);
  }


  public int getPort() {
    return this.port;
  }

  public URI getURI() {
    return UriBuilder.fromUri("https://localhost").port(port).build();
  }

  public String getKeyStore() {
    return "conf" + File.separator + "tls.p12";
  }

  public String getKeyStorePassword() {
    return keystorePassword;
  }

  public static AnalysisServerConfig load(File confFile) throws IOException {
    Properties props = new Properties();
    try (InputStream in = new FileInputStream(confFile)) {
      props.load(in);
    }
    return new AnalysisServerConfig(props);
  }

  public static AnalysisServerConfig loadDefault() throws IOException {
    return load(CONF_FILE);
  }

  private static String getNonEmptyProperty(Properties props, String key) {
    String value = props.getProperty(key);
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("The property " + key + " does not exist or is empty.");
    }
    return value.trim();
  }

}
