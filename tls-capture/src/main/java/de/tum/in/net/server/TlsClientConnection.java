package de.tum.in.net.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.tum.in.net.model.TlsResult;
import de.tum.in.net.model.TlsSocket;
import de.tum.in.net.util.Tap;

/**
 * Created by johannes on 14.05.17.
 */

class TlsClientConnection implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(TlsClientConnection.class);

  private final Socket socket;
  private final TlsServerFactory tlsServerFactory;

  public TlsClientConnection(final Socket socket, final TlsServerFactory tlsServerFactory) {
    this.socket = Objects.requireNonNull(socket, "socket must not be null.");
    this.tlsServerFactory =
        Objects.requireNonNull(tlsServerFactory, "tlsServerFactory must not be null.");
  }

  @Override
  public void run() {
    Tap tap = null;
    try (Socket s = socket) {
      tap = new Tap(socket.getInputStream(), socket.getOutputStream());

      try (final TlsSocket tlsSocket = tlsServerFactory.bind(tap.getIn(), tap.getOut())) {

        TlsResult tlsResult = new TlsResult(s, tap);

        InputStream is = tlsSocket.getInputStream();
        HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
        SessionInputBufferImpl buf = new SessionInputBufferImpl(metrics, 2048);
        buf.bind(is);

        DefaultHttpRequestParser reqParser = new DefaultHttpRequestParser(buf);
        try {
          HttpRequest req = reqParser.parse();

          if ("GET".equals(req.getRequestLine().getMethod())
              && "/".equals(req.getRequestLine().getUri())) {
            log.debug("Received new request: GET /");

            // add new line for easier parsing on client side
            String content = new Gson().toJson(tlsResult) + "\r\n";
            String response =
                "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: "
                    + content.length() + "\r\n\r\n" + content;

            PrintWriter writer = new PrintWriter(tlsSocket.getOutputStream());
            writer.write(response);
            writer.flush();

          } else {
            String response = "HTTP/1.1 404 Not Found";
            tlsSocket.getOutputStream().write(response.getBytes());
          }
        } catch (HttpException e) {
          e.printStackTrace();
        }

      }

    } catch (final IOException e) {
      log.error("IOError during test.", e);
    }

  }

}
