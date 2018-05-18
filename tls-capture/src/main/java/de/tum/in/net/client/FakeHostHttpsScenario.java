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
package de.tum.in.net.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.tls.TlsClient;

import de.tum.in.net.model.Step;


/**
 * Created by johannes on 31.03.17.
 */

public class FakeHostHttpsScenario extends AbstractScenario {

  private final TlsClient client;
  private final String host;

  public FakeHostHttpsScenario(HostAndPort target, String host) {
    this(target, host, new TlsDetectionClient(target.getHost()));
  }

  public FakeHostHttpsScenario(HostAndPort target, String host, TlsClient client) {
    super(target);
    this.host = Objects.requireNonNull(host);
    this.client = Objects.requireNonNull(client, "client must not be null");
  }

  @Override
  public String toString() {
    return FakeHostHttpsScenario.class.getName();
  }

  @Override
  public List<Step> getSteps() {
    return Arrays.asList(new TlsStep(client), new FakeHostHttpStep(host));
  }
}
