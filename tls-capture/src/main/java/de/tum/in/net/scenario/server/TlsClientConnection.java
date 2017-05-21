package de.tum.in.net.scenario.server;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

import de.tum.in.net.model.ResultListener;
import de.tum.in.net.model.Severity;
import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.model.TlsSocket;
import de.tum.in.net.scenario.ScenarioResult;

/**
 * Created by johannes on 14.05.17.
 */

class TlsClientConnection implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(TlsClientConnection.class);
  private static final byte[] OK = "OK".getBytes();
  private static final byte[] NOT_OK = "NOT_OK".getBytes();

  private final Socket socket;
  private final ResultListener publisher;
  private final TlsServerFactory tlsServerFactory;

  public TlsClientConnection(final Socket socket, final TlsServerFactory tlsServerFactory,
      final ResultListener publisher) {
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

      // first save the handshake
      final byte[] received = tap.getInputBytes();
      final byte[] sent = tap.getOutputytes();

      // now read the answer OK <-> NOT_OK
      final InputStream in = tlsSocket.getInputStream();
      final byte[] answer = IOUtils.toByteArray(in);

      if (Arrays.equals(answer, OK)) {
        publisher.publish(Severity.OK, null);
        // we do nothing
      } else if (Arrays.equals(answer, NOT_OK)) {
        // publish the handshake to the analysis server
        publisher.publish(Severity.NOT_OK,
            new ScenarioResult(socket.getRemoteSocketAddress().toString(), received, sent));
      } else {
        publisher.publish(Severity.ERROR, null);
        // TODO error in client or unknown client, what to do?
      }

      tlsSocket.close();


    } catch (final IOException e) {
      log.error("Socket closed.", e);
      publisher.publish(Severity.ERROR,
          new ScenarioResult(socket.getRemoteSocketAddress().toString(), "Error", e, tap));
    }

  }

}
