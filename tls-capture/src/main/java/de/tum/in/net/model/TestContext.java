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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestContext {

  private InputStream in;
  private OutputStream out;
  private Socket socket;
  private TlsResult tlsServerResult;
  private TlsResult tlsClientResult;

  public TestContext(Socket s) throws IOException {
    this.socket = s;
    this.in = socket.getInputStream();
    this.out = socket.getOutputStream();
  }

  public void setInputStream(InputStream in) {
    this.in = in;
  }

  public InputStream getInputStream() {
    return this.in;
  }

  public void setOutputStream(OutputStream out) {
    this.out = out;
  }

  public OutputStream getOutputStream() {
    return this.out;
  }

  public void setClientResult(TlsResult tlsClientResult) {
    this.tlsClientResult = tlsClientResult;
  }

  public void setServerResult(TlsResult tlsServerResult) {
    this.tlsServerResult = tlsServerResult;
  }

  public TlsResult getServerResult() {
    return this.tlsServerResult;
  }

  public TlsResult getClientResult() {
    return this.tlsClientResult;
  }

  public Socket getSocket() {
    return this.socket;
  }


}
