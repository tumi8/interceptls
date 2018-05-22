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

import java.io.Serializable;
import java.util.Objects;

public class HostAndPort implements Serializable {

  /**
   * 
   */
  private transient static final long serialVersionUID = -873403134912333123L;
  private final String host;
  private final int port;



  public HostAndPort(String host, int port) {
    this.host = Objects.requireNonNull(host);
    if (port < 0 || port > 65535) {
      throw new IllegalArgumentException("port must be between 0 and 65535");
    }
    this.port = port;
  }

  /**
   * Parse a string to {@link HostAndPort}. A string is valid if it is <host> or <host>:<port>.
   * 
   * @param hostAndPort
   * @throws IllegalArgumentException - if the provided string is not a valid host and port.
   * @return
   */
  public static HostAndPort parse(String hostAndPort) {
    String splitted[] = hostAndPort.split(":", 2);
    if (splitted.length == 1) {
      // we only have host
      return new HostAndPort(splitted[0], 443);
    } else if (splitted.length == 2) {
      // we have host and port
      try {
        int port = Integer.parseInt(splitted[1]);
        return new HostAndPort(splitted[0], port);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Port must be a number", e);
      }
    } else {
      throw new IllegalArgumentException("HostAndPort must be <host> or <host>:<port>");
    }

  }

  public String getHost() {
    return this.host;
  }

  public int getPort() {
    return this.port;
  }

  @Override
  public String toString() {
    return host + (port == 443 ? "" : (":" + port));
  }
}
