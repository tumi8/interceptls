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
