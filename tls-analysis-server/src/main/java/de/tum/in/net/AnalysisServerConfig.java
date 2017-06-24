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

  private final int port;

  public AnalysisServerConfig(int port) {
    this.port = port;
  }

  public AnalysisServerConfig(Properties props) {
    this.port = Integer.parseInt(getNonEmptyProperty(props, PORT));
  }


  public int getPort() {
    return this.port;
  }

  public URI getURI() {
    return UriBuilder.fromUri("http://localhost").port(port).build();
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
