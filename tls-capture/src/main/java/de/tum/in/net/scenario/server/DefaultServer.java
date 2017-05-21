package de.tum.in.net.scenario.server;

import org.bouncycastle.tls.AlertDescription;
import org.bouncycastle.tls.AlertLevel;
import org.bouncycastle.tls.DefaultTlsServer;
import org.bouncycastle.tls.TlsCredentialedSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Server which creates a RSA KeyPair and a corresponding X509Certificate which is served to the
 * clients. Created by johannes on 31.03.17.
 */
public class DefaultServer extends DefaultTlsServer {

  private static final Logger log = LoggerFactory.getLogger(DefaultServer.class);

  private final TlsServerConfig config;

  public DefaultServer(final TlsServerConfig config) {
    super(config.getCrypto());
    this.config = config;
    this.supportedCipherSuites = config.getCipherSuites();
  }

  @Override
  public void notifyAlertRaised(final short alertLevel, final short alertDescription,
      final String message, final Throwable cause) {
    super.notifyAlertRaised(alertLevel, alertDescription, message, cause);
    log.error("Raised alert, level: {}, description: {}", AlertLevel.getName(alertLevel),
        AlertDescription.getName(alertDescription), message, cause);
  }

  @Override
  public void notifyAlertReceived(final short alertLevel, final short alertDescription) {
    super.notifyAlertReceived(alertLevel, alertDescription);
    log.error("Received alert, level: {}, description: {}", AlertLevel.getName(alertLevel),
        AlertDescription.getName(alertDescription));
  }

  @Override
  protected TlsCredentialedSigner getRSASignerCredentials() throws IOException {
    return config.getRSASignerCredentials(context);
  }


  @Override
  protected TlsCredentialedSigner getECDSASignerCredentials() throws IOException {
    return config.getECDSASignerCredentials(context);
  }

}
