package de.tum.in.net.server;

import org.bouncycastle.tls.CipherSuite;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

import java.io.File;

import de.tum.in.net.scenario.server.TlsServerConfig;

import static org.junit.Assert.assertTrue;

/**
 * Created by johannes on 19.05.17.
 */

public class FileTlsServerConfigTest {

  @Test
  public void handleEC() throws Exception {
    final TlsServerConfig config =
        new FileTlsServerConfig(new File("certs/cert-ec.pem"), new File("certs/key-ec.pem"));
    assertTrue(Arrays.contains(config.getCipherSuites(),
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384));

  }

  @Test
  public void handleRSA() throws Exception {
    final TlsServerConfig config =
        new FileTlsServerConfig(new File("certs/cert-rsa.pem"), new File("certs/key-rsa.pem"));
    assertTrue(Arrays.contains(config.getCipherSuites(),
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384));
  }
}
