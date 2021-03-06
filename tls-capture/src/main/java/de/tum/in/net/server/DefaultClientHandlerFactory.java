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

import java.net.Socket;

/**
 * Created by johannes on 17.05.17.
 */

public class DefaultClientHandlerFactory implements ClientHandlerFactory {

  private final TlsServerFactory tlsServerFactory;
  private final String redirectUrl;

  public DefaultClientHandlerFactory(final TlsServerFactory tlsServerFactory) {
    this(tlsServerFactory, null);
  }

  public DefaultClientHandlerFactory(final TlsServerFactory tlsServerFactory, String redirectUrl) {
    this.tlsServerFactory = tlsServerFactory;
    this.redirectUrl = redirectUrl;
  }

  @Override
  public Runnable createClientHandler(final Socket client) {
    return new TlsClientConnection(client, tlsServerFactory, redirectUrl);
  }
}
