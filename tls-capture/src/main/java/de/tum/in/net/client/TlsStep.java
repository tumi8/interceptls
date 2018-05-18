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

import java.io.IOException;

import org.bouncycastle.tls.TlsClient;
import org.bouncycastle.tls.TlsClientProtocol;

import de.tum.in.net.model.Step;
import de.tum.in.net.model.TestContext;
import de.tum.in.net.model.TlsAbortException;
import de.tum.in.net.model.TlsResult;
import de.tum.in.net.util.Tap;

public class TlsStep implements Step {

  private final TlsClient client;

  public TlsStep(TlsClient client) {
    this.client = client;
  }

  @Override
  public void process(HostAndPort target, TestContext ctx) throws IOException, TlsAbortException {

    Tap tap = new Tap(ctx.getInputStream(), ctx.getOutputStream());

    // connect in blocking mode
    final TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(tap.getIn(), tap.getOut());
    try {
      tlsClientProtocol.connect(client);
      // we are now connected, therefore we can publish the captured bytes
      TlsResult result = new TlsResult(ctx.getSocket(), tap);
      ctx.setClientResult(result);

      ctx.setInputStream(tlsClientProtocol.getInputStream());
      ctx.setOutputStream(tlsClientProtocol.getOutputStream());
    } catch (IOException e) {
      TlsResult result = new TlsResult(ctx.getSocket(), tap);
      throw new TlsAbortException(result, "Exception while TLS negotation.", e);
    }


  }

}
