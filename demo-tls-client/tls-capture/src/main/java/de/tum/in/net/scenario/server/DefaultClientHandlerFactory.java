package de.tum.in.net.scenario.server;

import java.net.Socket;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.TlsServerFactory;

/**
 * Created by johannes on 17.05.17.
 */

public class DefaultClientHandlerFactory implements ClientHandlerFactory {

    private final ResultListener publisher;
    private final TlsServerFactory tlsServerFactory;

    public DefaultClientHandlerFactory(final TlsServerFactory tlsServerFactory, final ResultListener publisher) {
        this.tlsServerFactory = tlsServerFactory;
        this.publisher = publisher;
    }

    @Override
    public Runnable createClientHandler(final Socket client) {
        return new TlsClientConnection(client, tlsServerFactory, publisher);
    }
}
