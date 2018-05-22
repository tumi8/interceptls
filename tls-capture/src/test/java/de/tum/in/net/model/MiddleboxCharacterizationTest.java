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
package de.tum.in.net.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.bouncycastle.tls.ProtocolVersion;
import org.junit.Test;

public class MiddleboxCharacterizationTest {

  @Test
  public void simple() {
    MiddleboxCharacterization.Builder b = new MiddleboxCharacterization.Builder();

    b.setCanConnectWrongHttpHost(true);
    b.setCanConnectWrongSni(false);
    b.setVersionSupport(ProtocolVersion.TLSv10);
    b.setVersionSupport(ProtocolVersion.TLSv11);

    MiddleboxCharacterization c = b.build();

    assertTrue(c.getCanConnectWrongHttpHost());
    assertFalse(c.getCanConnectWrongSni());

    assertFalse(c.isSslV3());
    assertTrue(c.isTlsV10());
    assertTrue(c.isTlsV11());
    assertFalse(c.isTlsV12());
    assertEquals("[TLS 1.0, TLS 1.1]", c.getSupportedTlsVersions().toString());
  }
}
