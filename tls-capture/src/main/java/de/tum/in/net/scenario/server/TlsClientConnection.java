package de.tum.in.net.scenario.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.model.TlsSocket;
import de.tum.in.net.model.TlsTestId;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

/**
 * Created by johannes on 14.05.17.
 */

class TlsClientConnection implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(TlsClientConnection.class);

  private final Socket socket;
  private final ResultListener<ScenarioResult> publisher;
  private final TlsServerFactory tlsServerFactory;

  public TlsClientConnection(final Socket socket, final TlsServerFactory tlsServerFactory,
      final ResultListener<ScenarioResult> publisher) {
    this.socket = Objects.requireNonNull(socket, "socket must not be null.");
    this.tlsServerFactory =
        Objects.requireNonNull(tlsServerFactory, "tlsServerFactory must not be null.");
    this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
  }

  @Override
  public void run() {
    Tap tap = null;
    try {
      tap = new Tap(socket.getInputStream(), socket.getOutputStream());

      final TlsSocket tlsSocket = tlsServerFactory.bind(tap.getIn(), tap.getOut());

      ScenarioResultBuilder builder = new ScenarioResultBuilder(socket).transmitted(tap);

      // we always require the session-id
      TlsTestId id = TlsTestId.read(tlsSocket.getInputStream());
      ScenarioResult result = builder.connected(id.getTestId());

      publisher.publish(id.getTestSessionId(), result);


      tlsSocket.close();


    } catch (final IOException e) {
      log.error("Socket closed.", e);
      ScenarioResult result = new ScenarioResultBuilder(socket).transmitted(tap).error(e,
          TlsTestId.randomID().getTestId());
      publisher.publish(null, result);
    }

  }

}
