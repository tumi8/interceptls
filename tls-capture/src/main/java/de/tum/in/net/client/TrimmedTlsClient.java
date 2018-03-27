package de.tum.in.net.client;

import java.io.IOException;
import java.security.SecureRandom;

import org.bouncycastle.tls.CipherSuite;
import org.bouncycastle.tls.DefaultTlsClient;
import org.bouncycastle.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.tls.TlsAuthentication;
import org.bouncycastle.tls.TlsServerCertificate;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrimmedTlsClient extends DefaultTlsClient {

  private static final Logger log = LoggerFactory.getLogger(TrimmedTlsClient.class);
  private static final TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());

  public TrimmedTlsClient() {
    super(crypto);
    // similar to firefox cipher suites
    this.supportedCipherSuites = new int[] {CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
        CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA,
        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA

    };



  }

  @Override
  public TlsAuthentication getAuthentication() throws IOException {
    return new ServerOnlyTlsAuthentication() {
      @Override
      public void notifyServerCertificate(final TlsServerCertificate serverCertificate)
          throws IOException {
        log.debug("Notify server certificate.");
      }
    };
  }

}
