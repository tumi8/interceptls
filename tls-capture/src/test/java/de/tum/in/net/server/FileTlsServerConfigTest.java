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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.bouncycastle.tls.CipherSuite;
import org.bouncycastle.util.Arrays;
import org.junit.Test;

/**
 * Created by johannes on 19.05.17.
 */

public class FileTlsServerConfigTest {

  @Test
  public void handleEC() throws Exception {
    final TlsServerConfig config =
        new FileTlsServerConfig(new File("certs/analysis-server-cert-ec.pem.chain"),
            new File("certs/analysis-server-key-ec.pem"));
    assertTrue(Arrays.contains(config.getCipherSuites(),
        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384));

  }

  @Test
  public void handleRSA() throws Exception {
    final TlsServerConfig config =
        new FileTlsServerConfig(new File("certs/analysis-server-cert-rsa.pem.chain"),
            new File("certs/analysis-server-key-rsa.pem"));
    assertTrue(Arrays.contains(config.getCipherSuites(),
        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384));
  }
}
