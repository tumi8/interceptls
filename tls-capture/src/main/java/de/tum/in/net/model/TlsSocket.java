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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.bouncycastle.tls.TlsServerProtocol;

/**
 * Created by johannes on 18.05.17.
 */

public class TlsSocket extends Socket implements Closeable {

  private final TlsServerProtocol protocol;

  public TlsSocket(final TlsServerProtocol protocol) {
    this.protocol = protocol;
  }

  @Override
  public InputStream getInputStream() {
    return protocol.getInputStream();
  }

  @Override
  public OutputStream getOutputStream() {
    return protocol.getOutputStream();
  }

  @Override
  public void close() throws IOException {
    protocol.close();
  }
}
