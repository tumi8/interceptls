package de.tum.in.net.scenario.server;

import org.bouncycastle.tls.TlsServerProtocol;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

import de.tum.in.net.model.Tap;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultServerScenario implements Scenario {

    private static final Logger log = LoggerFactory.getLogger(DefaultServerScenario.class);
    private final int port;
    private final byte[] buffer;

    public DefaultServerScenario(final int port, final int expectedBytes) {
        this.port = port;
        this.buffer = new byte[expectedBytes];
    }

    public byte[] getReceivedBytes() {
        return buffer;
    }

    @Override
    public ScenarioResult call() {
        ScenarioResult result;
        Tap tap = null;
        try (ServerSocket server = new ServerSocket(port)) {
            final Socket s = server.accept();
            tap = new Tap(s.getInputStream(), s.getOutputStream());

            //connect in blocking mode
            final TlsServerProtocol tlsServerProtocol = new TlsServerProtocol(tap.getIn(), tap.getOut());
            tlsServerProtocol.accept(new DefaultServer(new BcTlsCrypto(new SecureRandom())));

            // we are now connected, therefore we can publish the captured bytes
            result = new ScenarioResult("Server", tap.getInputBytes(), tap.getOutputytes());

            tlsServerProtocol.getInputStream().read(buffer);

            return result;
        } catch (final IOException e) {
            result = new ScenarioResult("Server", "Error in " + toString(), e, tap);
        }
        return result;
    }

    @Override
    public String toString() {
        return DefaultServerScenario.class.getName();
    }
}
