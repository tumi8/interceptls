package de.tum.in.net.scenario.client;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Objects;

import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TlsTestId;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenario implements Scenario {

  private static final Logger log = LoggerFactory.getLogger(DefaultClientScenario.class);
  private final TlsTestId id;
  private final String destination;
  private final int port;


  public DefaultClientScenario(TlsTestId id, final String destination, final int port) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.destination = Objects.requireNonNull(destination, "destination must not be null.");
    this.port = port;
  }

  @Override
  public ScenarioResult call() {
    ScenarioResult result;
    Tap tap = null;
    log.debug("Trying to connect to {}:{}", destination, port);
    try (Socket s = new Socket(destination, port)) {
      tap = new Tap(s.getInputStream(), s.getOutputStream());

      // connect in blocking mode
      final TlsClientProtocol tlsClientProtocol =
          new TlsClientProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
      tlsClientProtocol.connect(new DefaultTlsClient() {
        @Override
        public TlsAuthentication getAuthentication() throws IOException {
          return new ServerOnlyTlsAuthentication() {
            @Override
            public void notifyServerCertificate(final Certificate serverCertificate)
                throws IOException {
              log.debug("Notify server certificate.");
            }
          };
        }
      });

      // we are now connected, therefore we can publish the captured bytes
      result = new ScenarioResultBuilder(s).sent(tap.getOutputytes()).received(tap.getInputBytes())
          .connected(id.getTestId());

      // then we send our session-id
      tlsClientProtocol.getOutputStream().write(id.getTransmitBytes());

      tlsClientProtocol.close();

    } catch (final IOException e) {
      log.warn("Error in " + toString(), e);

      result = new ScenarioResultBuilder("Client", destination).transmitted(tap).error(e,
          id.getTestId());
    }

    return result;

  }

  @Override
  public String toString() {
    return DefaultClientScenario.class.getName();
  }
}
