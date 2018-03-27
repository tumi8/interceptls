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
