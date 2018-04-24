package de.tum.in.net.session;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import de.tum.in.net.util.CertificateUtil;

public class AnalysisTlsContextTest {

  @Test
  public void canCreateSSLContext() {
    SSLContext ctx = AnalysisTlsContext.createContext();
    assertArrayEquals(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"},
        ctx.getDefaultSSLParameters().getProtocols());
  }

  @Test
  public void canValidateCertificate() throws Exception {
    X509TrustManager tm = AnalysisTlsContext.getTrustManager();

    X509Certificate certRsa =
        CertificateUtil.readCert(new File("certs/analysis-server-cert-rsa.pem"));
    tm.checkServerTrusted(new X509Certificate[] {certRsa}, "RSA");

    X509Certificate certEc =
        CertificateUtil.readCert(new File("certs/analysis-server-cert-ec.pem"));
    tm.checkServerTrusted(new X509Certificate[] {certEc}, "RSA");
  }

  @Test
  public void verifiesAllHostnames() throws Exception {
    HostnameVerifier hv = AnalysisTlsContext.getHostnameVerifier();
    assertTrue(hv.verify("net.in.tum.de", null));
    assertTrue(hv.verify(null, null));
  }

}
