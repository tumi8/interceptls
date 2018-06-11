/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.server;

import java.io.IOException;

import org.bouncycastle.tls.AlertDescription;
import org.bouncycastle.tls.AlertLevel;
import org.bouncycastle.tls.DefaultTlsServer;
import org.bouncycastle.tls.ProtocolVersion;
import org.bouncycastle.tls.TlsCredentialedSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  protected ProtocolVersion getMaximumVersion() {
    return ProtocolVersion.TLSv12;
  }

  @Override
  protected ProtocolVersion getMinimumVersion() {
    return ProtocolVersion.TLSv10;
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
