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
package de.tum.in.net.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class CertificateUtil {

  public static X509Certificate[] readCerts(InputStream stream)
      throws IOException, CertificateException {
    try (final InputStream in = stream) {
      final CertificateFactory factory = CertificateFactory.getInstance("X.509");
      return factory.generateCertificates(in).toArray(new X509Certificate[0]);
    }
  }

  public static X509Certificate[] readCerts(File file) throws IOException, CertificateException {
    try (final InputStream in = new FileInputStream(file)) {
      final CertificateFactory factory = CertificateFactory.getInstance("X.509");
      return factory.generateCertificates(in).toArray(new X509Certificate[0]);
    }
  }

  public static X509Certificate readCert(InputStream stream)
      throws IOException, CertificateException {
    try (final InputStream in = stream) {
      final CertificateFactory factory = CertificateFactory.getInstance("X.509");
      return (X509Certificate) factory.generateCertificate(in);
    }
  }

  public static X509Certificate readCert(File file) throws IOException, CertificateException {
    try (final InputStream in = new FileInputStream(file)) {
      final CertificateFactory factory = CertificateFactory.getInstance("X.509");
      return (X509Certificate) factory.generateCertificate(in);
    }
  }

}
