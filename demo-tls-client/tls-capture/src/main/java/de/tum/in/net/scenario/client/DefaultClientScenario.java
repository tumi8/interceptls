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

import de.tum.in.net.Tap;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.server.DefaultServerScenario;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenario implements Scenario {

    private static final Logger log = LoggerFactory.getLogger(DefaultClientScenario.class);
    private String destination;
    private int port;
    private byte[] transmit;

    public DefaultClientScenario(String destination, int port) {
        this(destination, port, null);
    }

    public DefaultClientScenario(String destination, int port, byte[] transmit) {
        this.destination = Objects.requireNonNull(destination, "destination must not be null.");
        this.port = port;
        this.transmit = transmit;
    }

    @Override
    public ScenarioResult call(){
        ScenarioResult result;
        Tap tap = null;
        try (Socket s = new Socket(destination, port)) {
            tap = new Tap(s.getInputStream(), s.getOutputStream());

            //connect in blocking mode
            TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
            tlsClientProtocol.connect(new DefaultTlsClient() {
                @Override
                public TlsAuthentication getAuthentication() throws IOException {
                    return new ServerOnlyTlsAuthentication() {
                        @Override
                        public void notifyServerCertificate(Certificate serverCertificate) throws IOException {

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

        } catch (IOException e) {
            log.warn("Error in " +  toString(), e);
            result =  new ScenarioResult("", e, tap);
        }

        return result;

    }

    @Override
    public String toString(){
        return DefaultClientScenario.class.getName();
    }
}
