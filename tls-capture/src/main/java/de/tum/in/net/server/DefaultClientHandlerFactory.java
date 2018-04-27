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
