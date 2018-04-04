package de.tum.in.net.client;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Vector;

import org.bouncycastle.tls.CipherSuite;
import org.bouncycastle.tls.DefaultTlsClient;
import org.bouncycastle.tls.NameType;
import org.bouncycastle.tls.ProtocolVersion;
import org.bouncycastle.tls.ServerName;
import org.bouncycastle.tls.TlsAuthentication;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

public class TlsDetectionClient extends DefaultTlsClient {

  private static final TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());
  private final String sni;

  // for the detection we support old TLS versions and old ciphersuites, otherwise we might not
  // detect legacy middleboxes
  public TlsDetectionClient(String sni) {
    super(crypto);
    this.sni = sni;
    /*
     * Use modern cipher suites from https://wiki.mozilla.org/Security/Server_Side_TLS as of
     * 28.03.2018
     */
    this.supportedCipherSuites = new int[] {
        CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
        CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
        CipherSuite.TLS_DHE_RSA_WITH_AES_256_GCM_SHA384,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA256,
        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA256,
        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA,
        CipherSuite.TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA,
        CipherSuite.TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA,
        CipherSuite.TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
        CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256, CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA};

  }


  @Override
  public ProtocolVersion getMinimumVersion() {
    return ProtocolVersion.TLSv10;
  }

  @Override
  public ProtocolVersion getClientVersion() {
    return ProtocolVersion.TLSv12;
  }

  @Override
  public TlsAuthentication getAuthentication() throws IOException {
    return new IgnoreServerCertAuthentication();
  }

  @Override
  protected Vector<?> getSNIServerNames() {
    if (sni == null) {
      return null;
    }
    final ServerName sn = new ServerName(NameType.host_name, sni);
    final Vector<ServerName> vlist = new Vector<>(1);
    vlist.add(sn);
    return vlist;
  }

}