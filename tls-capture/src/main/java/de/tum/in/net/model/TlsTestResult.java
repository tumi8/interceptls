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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDateTime;

public class TlsTestResult implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = 5955734350476053070L;

  private final String timestamp = LocalDateTime.now().toString();
  private final NetworkId network;
  private final List<TlsClientServerResult> results;
  private MiddleboxCharacterization middleboxCharacterization;

  public TlsTestResult(NetworkId network, List<TlsClientServerResult> results) {
    this.network = Objects.requireNonNull(network);
    this.results = Objects.requireNonNull(results);
  }

  public boolean anySuccessfulConnection() {
    return successfulConnections() > 0;
  }

  public int successfulConnections() {
    int counter = 0;
    for (TlsClientServerResult result : results) {
      if (result.isSuccess()) {
        counter++;
      }
    }
    return counter;
  }

  public int interceptions() {
    int counter = 0;
    for (TlsClientServerResult result : results) {
      if (result.isSuccess() && result.isIntercepted()) {
        counter++;
      }
    }
    return counter;
  }

  public boolean anyInterception() {
    return interceptions() > 0;
  }

  public NetworkId getNetworkId() {
    return network;
  }

  public List<TlsClientServerResult> getClientServerResults() {
    return Collections.unmodifiableList(results);
  }

  public String getTimestamp() {
    return timestamp;
  }

  /**
   * 
   * @throws IllegalStateException - if no interception occured
   * @return the first target for which an interception is detected.
   */
  public TlsClientServerResult getInterceptedTarget() {
    for (TlsClientServerResult result : results) {
      if (result.isSuccess() && result.isIntercepted()) {
        return result;
      }
    }
    throw new IllegalStateException("No intercepted connection found.");
  }

  public void setMiddleboxCharacterization(MiddleboxCharacterization c) {
    this.middleboxCharacterization = c;
  }

  public MiddleboxCharacterization getMiddleboxCharacterization() {
    if (!anyInterception()) {
      throw new IllegalStateException(
          "The middlebox is characterised iff an interception was detected.");
    }
    if (middleboxCharacterization == null) {
      throw new IllegalStateException(
          "Internal error. Despite interception no characterization is available.");
    }
    return middleboxCharacterization;
  }

}
