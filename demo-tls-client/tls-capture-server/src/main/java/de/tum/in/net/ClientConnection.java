package de.tum.in.net;

import org.bouncycastle.crypto.tls.TlsServerProtocol;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;

import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 14.05.17.
 */

class ClientConnection implements Scenario {

    private final Socket socket;

    public ClientConnection(final Socket socket) {
        this.socket = socket;
    }

    @Override
    public ScenarioResult call() {
        Tap tap = null;
        try {
            tap = new Tap(socket.getInputStream(), socket.getOutputStream());

            final TlsServerProtocol protocol = new TlsServerProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
            protocol.accept(new DefaultServer());

            return new ScenarioResult(socket.getRemoteSocketAddress().toString(), tap.getInputBytes(), tap.getOutputytes());

        } catch (final IOException e) {
            return new ScenarioResult(socket.getRemoteSocketAddress().toString(), "Error", e, tap);
        }
        
    }
}
