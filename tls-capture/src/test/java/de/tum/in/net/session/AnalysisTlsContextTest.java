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
package de.tum.in.net.session;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Constructor;
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

    X509TrustManager tm2 = AnalysisTlsContext.getTrustManager();
    assertEquals(tm, tm2);
  }

  @Test
  public void verifiesAllHostnames() throws Exception {
    HostnameVerifier hv = AnalysisTlsContext.getHostnameVerifier();
    assertTrue(hv.verify("net.in.tum.de", null));
    assertTrue(hv.verify(null, null));
  }

  @Test
  public void coverage() throws Exception {
    Constructor<AnalysisTlsContext> c = AnalysisTlsContext.class.getDeclaredConstructor();
    c.setAccessible(true);
    c.newInstance();
  }

}
