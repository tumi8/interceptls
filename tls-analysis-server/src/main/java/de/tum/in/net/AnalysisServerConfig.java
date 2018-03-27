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
  private static final File KEYSTORE_FILE = new File("conf", "tls.p12");
  private static final String PORT = "port";
  private static final String TLS_KEYSTORE_PASSWORD = "tls.keystore.password";

  private static final String DB_USER = "db.user";
  private static final String DB_PASSWORD = "db.password";
  private static final String DB_TARGET = "db.target";

  private final int port;
  private final String keystorePassword;
  private final String dbUser;
  private final String dbPassword;
  private final String dbTarget;

  public AnalysisServerConfig(int port, String keystorePassword, String dbUser, String dbPassword,
      String dbTarget) {
    this.port = port;
    this.keystorePassword = keystorePassword;
    this.dbUser = dbUser;
    this.dbPassword = dbPassword;
    this.dbTarget = dbTarget;
  }

  public AnalysisServerConfig(Properties props) {
    this.port = Integer.parseInt(getNonEmptyProperty(props, PORT));
    this.keystorePassword = getNonEmptyProperty(props, TLS_KEYSTORE_PASSWORD);
    this.dbUser = getNonEmptyProperty(props, DB_USER);
    this.dbPassword = props.getProperty(DB_PASSWORD, "");
    this.dbTarget = getNonEmptyProperty(props, DB_TARGET);
  }


  public int getPort() {
    return this.port;
  }

  public URI getURI() {
    return UriBuilder.fromUri("https://0.0.0.0").port(port).build();
  }

  public String getKeyStore() {
    return KEYSTORE_FILE.getAbsolutePath();
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

  public String getDbUser() {
    return dbUser;
  }

  public String getDbPassword() {
    return dbPassword;
  }

  public String getDbTarget() {
    return dbTarget;
  }

}
