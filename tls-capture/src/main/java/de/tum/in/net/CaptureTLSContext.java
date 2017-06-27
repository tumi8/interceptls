package de.tum.in.net;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CaptureTLSContext {

  private static final SecureRandom random = new SecureRandom();

  private static final HostnameVerifier UNSAFE_HOSTNAME_VERIFIER = new HostnameVerifier() {

    @Override
    public boolean verify(String hostname, SSLSession session) {
      // trust all
      return true;
    }
  };

  private static final TrustManager[] UNSAFE_TRUST_MANAGERS =
      new TrustManager[] {new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
          // TODO Auto-generated method stub

        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
          // TODO Auto-generated method stub

        }
      }};


  public static SSLContext createContext() {

    try {
      SSLContext ctx = SSLContext.getInstance("TLSv1.2");
      ctx.init(null, UNSAFE_TRUST_MANAGERS, random);
      return ctx;
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new IllegalStateException("Unexpected error", e);
    }
  }

  public static HostnameVerifier getHostnameVerifier() {
    return UNSAFE_HOSTNAME_VERIFIER;
  }

}
