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

import org.bouncycastle.tls.TlsCredentialedSigner;
import org.bouncycastle.tls.TlsServerContext;
import org.bouncycastle.tls.crypto.TlsCrypto;

import java.io.IOException;

/**
 * Created by johannes on 19.05.17.
 */

public interface TlsServerConfig {

  TlsCrypto getCrypto();

  TlsCredentialedSigner getRSASignerCredentials(TlsServerContext context) throws IOException;

  TlsCredentialedSigner getECDSASignerCredentials(TlsServerContext context) throws IOException;

  int[] getCipherSuites();
}
