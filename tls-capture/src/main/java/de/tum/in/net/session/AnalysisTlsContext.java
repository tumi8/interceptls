package de.tum.in.net.session;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.util.CertificateUtil;

/**
 * This class contains the TLS settings to be used for the connection to the TLS Analysis Server.
 * The connection is secured by only trusting pinned certificates.
 * 
 * @author johannes
 *
 */
public class AnalysisTlsContext {

  private static final Logger log = LoggerFactory.getLogger(AnalysisTlsContext.class);
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static boolean initialized = false;
  private static X509TrustManager trustManager;

  private static final HostnameVerifier UNSAFE_HOSTNAME_VERIFIER = new HostnameVerifier() {
    @Override
    public boolean verify(String hostname, SSLSession session) {
      // trust all, we rely on the pinned root certificate
      return true;
    }
  };

  private AnalysisTlsContext() {
    // utility
  }

  private synchronized static void init() {
    try {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(null);

      try (InputStream in =
          AnalysisTlsContext.class.getClassLoader().getResourceAsStream("trusted-certs.pem")) {
        X509Certificate[] certs = CertificateUtil.readCerts(in);
        log.debug("Loaded {} trusted certificates.", certs.length);
        int i = 1;
        for (X509Certificate cert : certs) {
          KeyStore.TrustedCertificateEntry e = new KeyStore.TrustedCertificateEntry(cert);
          keyStore.setEntry("ca-" + i, e, null);
          i++;
        }

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] trustManagers = tmf.getTrustManagers();
        log.debug("tm size: {}", trustManagers.length);
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
          throw new IllegalStateException("Unexpected default trust managers");
        }
        trustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        initialized = true;
      }
    } catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException e) {
      throw new IllegalStateException("Could not initialize AnalysisTlsContext", e);
    }
  }

  public static X509TrustManager getTrustManager() {
    checkInitialized();
    return trustManager;
  }

  public static SSLContext createContext() {
    checkInitialized();
    try {
      SSLContext ctx = SSLContext.getInstance("TLSv1.2");
      ctx.init(null, new TrustManager[] {trustManager}, SECURE_RANDOM);
      return ctx;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new IllegalStateException("Could not init SSLContext", e);
    }
  }

  public static SSLSocketFactory createSocketFactory() {
    return createContext().getSocketFactory();
  }

  private static void checkInitialized() {
    if (!initialized) {
      init();
    }
  }

  public static HostnameVerifier getHostnameVerifier() {
    return UNSAFE_HOSTNAME_VERIFIER;
  }

}
