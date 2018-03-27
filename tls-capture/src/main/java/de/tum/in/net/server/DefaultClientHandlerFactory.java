package de.tum.in.net.server;

import java.net.Socket;

/**
 * Created by johannes on 17.05.17.
 */

public class DefaultClientHandlerFactory implements ClientHandlerFactory {

  private final TlsServerFactory tlsServerFactory;

  public DefaultClientHandlerFactory(final TlsServerFactory tlsServerFactory) {
    this.tlsServerFactory = tlsServerFactory;
  }

  @Override
  public Runnable createClientHandler(final Socket client) {
    return new TlsClientConnection(client, tlsServerFactory);
  }
}
