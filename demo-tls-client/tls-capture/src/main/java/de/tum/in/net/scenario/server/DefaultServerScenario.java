package de.tum.in.net.scenario.server;

import org.bouncycastle.crypto.tls.TlsServerProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

import de.tum.in.net.DefaultServer;
import de.tum.in.net.Tap;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultServerScenario implements Scenario {

    private static final Logger log = LoggerFactory.getLogger(DefaultServerScenario.class);
    private int port;
    private byte[] buffer;

    public DefaultServerScenario(int port, int expectedBytes) {
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
            Socket s = server.accept();
            tap = new Tap(s.getInputStream(), s.getOutputStream());

            //connect in blocking mode
            TlsServerProtocol tlsServerProtocol = new TlsServerProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
            tlsServerProtocol.accept(new DefaultServer());

            // we are now connected, therefore we can publish the captured bytes
            result = new ScenarioResult("Server", tap.getInputBytes(), tap.getOutputytes());

            tlsServerProtocol.getInputStream().read(buffer);

            return result;
        } catch (IOException e) {
            result = new ScenarioResult("Error in " + toString(), e, tap);
        }
        return result;
    }

    @Override
    public String toString() {
        return DefaultServerScenario.class.getName();
    }
}
