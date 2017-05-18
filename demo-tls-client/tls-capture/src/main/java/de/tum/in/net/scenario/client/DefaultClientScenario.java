package de.tum.in.net.scenario.client;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Objects;

import de.tum.in.net.model.Tap;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenario implements Scenario {

    private static final Logger log = LoggerFactory.getLogger(DefaultClientScenario.class);
    private final String destination;
    private final int port;
    private final byte[] transmit;

    public DefaultClientScenario(final String destination, final int port) {
        this(destination, port, null);
    }

    public DefaultClientScenario(final String destination, final int port, final byte[] transmit) {
        this.destination = Objects.requireNonNull(destination, "destination must not be null.");
        this.port = port;
        this.transmit = transmit;
    }

    @Override
    public ScenarioResult call() {
        ScenarioResult result;
        Tap tap = null;
        log.debug("Trying to connect to {}:{}", destination, port);
        try (Socket s = new Socket(destination, port)) {
            tap = new Tap(s.getInputStream(), s.getOutputStream());

            //connect in blocking mode
            final TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
            tlsClientProtocol.connect(new DefaultTlsClient() {
                @Override
                public TlsAuthentication getAuthentication() throws IOException {
                    return new ServerOnlyTlsAuthentication() {
                        @Override
                        public void notifyServerCertificate(final Certificate serverCertificate) throws IOException {

                        }
                    };
                }
            });

            // we are now connected, therefore we can publish the captured bytes
            result = new ScenarioResult(destination, tap.getInputBytes(), tap.getOutputytes());

            // then we can send additional bytes
            if (transmit != null) {
                tlsClientProtocol.getOutputStream().write(transmit);
            }

        } catch (final IOException e) {
            log.warn("Error in " + toString(), e);
            result = new ScenarioResult(destination, "IOError", e, tap);
        }

        return result;

    }

    @Override
    public String toString() {
        return DefaultClientScenario.class.getName();
    }
}
