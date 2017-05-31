package de.tum.in.net.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtil {

  public static X509Certificate[] readChain(File file) throws IOException, CertificateException {
    try (final InputStream in = new FileInputStream(file)) {
      final CertificateFactory factory = CertificateFactory.getInstance("X.509");
      return factory.generateCertificates(in).toArray(new X509Certificate[0]);
    }
  }

  public static X509Certificate readCert(File file) throws IOException, CertificateException {
    try (final InputStream in = new FileInputStream(file)) {
      final CertificateFactory factory = CertificateFactory.getInstance("X.509");
      return (X509Certificate) factory.generateCertificate(in);
    }
  }

}
