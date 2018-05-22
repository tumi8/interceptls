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

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.tum.in.net.model.NetworkId;
import de.tum.in.net.model.TestSession;
import de.tum.in.net.model.TlsClientServerResult;
import de.tum.in.net.model.TlsTestResult;

public class OnlineTestSessionTest {

  @Test(expected = ConnectException.class)
  public void noConnection() throws Exception {
    TestSession s = new OnlineTestSession("https://127.0.0.1:64523");
    List<TlsClientServerResult> results = Arrays.asList();
    TlsTestResult r = new TlsTestResult(new NetworkId(), results);
    s.uploadResult(r);
  }

  @Test(expected = IllegalArgumentException.class)
  public void cannotUploadNull() throws Exception {
    TestSession s = new OnlineTestSession("https://127.0.0.1:3000");
    s.uploadResult(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalUrl() {
    new OnlineTestSession("bla bla");
  }
}
