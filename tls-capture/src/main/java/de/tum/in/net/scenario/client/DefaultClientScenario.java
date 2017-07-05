package de.tum.in.net.scenario.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import org.bouncycastle.tls.TlsClient;
import org.bouncycastle.tls.TlsClientProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.Node;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

/**
 * Created by johannes on 31.03.17.
 */

public class DefaultClientScenario implements Scenario {

  private static final Logger log = LoggerFactory.getLogger(DefaultClientScenario.class);
  private final TestID id;
  private final String destination;
  private final int port;
  private final TlsClient client;


  public DefaultClientScenario(TestID id, final String destination, final int port) {
    this(id, destination, port, new TrimmedTlsClient());
  }

  public DefaultClientScenario(TestID id, final String destination, final int port,
      TlsClient client) {
    this.id = Objects.requireNonNull(id, "id must not be null");
    this.destination = Objects.requireNonNull(destination, "destination must not be null.");
    this.port = port;
    this.client = Objects.requireNonNull(client, "client must not be null");
  }

  @Override
  public ScenarioResult call() {
    ScenarioResult result;
    Tap tap = null;
    log.debug("Trying to connect to {}:{}", destination, port);
    try (Socket s = new Socket(destination, port)) {
      tap = new Tap(s.getInputStream(), s.getOutputStream());

      // connect in blocking mode
      final TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(tap.getIn(), tap.getOut());
      tlsClientProtocol.connect(client);

      // we are now connected, therefore we can publish the captured bytes
      result = new ScenarioResultBuilder(Node.CLIENT, s).sent(tap.getOutputytes())
          .received(tap.getInputBytes()).connected();

      // then we send our session-id
      tlsClientProtocol.getOutputStream().write(id.getTransmitBytes());

      tlsClientProtocol.close();

    } catch (final IOException e) {
      log.warn("Error in " + toString(), e);

      result =
          new ScenarioResultBuilder(Node.CLIENT, "Client", destination).transmitted(tap).error(e);
    }

    return result;

  }

  @Override
  public String toString() {
    return DefaultClientScenario.class.getName();
  }

  @Override
  public TestID getTestID() {
    return id;
  }
}
