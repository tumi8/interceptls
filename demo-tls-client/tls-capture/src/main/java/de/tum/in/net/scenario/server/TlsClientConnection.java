package de.tum.in.net.scenario.server;

import java.io.IOException;
import java.net.Socket;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 14.05.17.
 */

class TlsClientConnection implements Runnable {

    private final Socket socket;
    private final ResultListener publisher;
    private final TlsServerFactory tlsServerFactory;

    public TlsClientConnection(final Socket socket, final TlsServerFactory tlsServerFactory, final ResultListener publisher) {
        this.socket = socket;
        this.tlsServerFactory = tlsServerFactory;
        this.publisher = publisher;
    }

    @Override
    public void run() {
        Tap tap = null;
        try {
            tap = new Tap(socket.getInputStream(), socket.getOutputStream());

            tlsServerFactory.bind(tap.getIn(), tap.getOut());

            publisher.publish(new ScenarioResult(socket.getRemoteSocketAddress().toString(), tap.getInputBytes(), tap.getOutputytes()));

        } catch (final IOException e) {
            publisher.publish(new ScenarioResult(socket.getRemoteSocketAddress().toString(), "Error", e, tap));
        }

    }
}
