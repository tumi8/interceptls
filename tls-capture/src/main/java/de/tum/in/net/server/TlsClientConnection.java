/**
 * Copyright © 2018 Johannes Schleger (johannes.schleger@tum.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tum.in.net.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import de.tum.in.net.model.TlsConstants;
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
  private final String redirectUrl;

  /**
   * 
   * @param socket
   * @param tlsServerFactory
   * @param redirectUrl - the url to which we redirect, may be null, then a 404 Not Found is sent
   *        instead.
   */
  public TlsClientConnection(final Socket socket, final TlsServerFactory tlsServerFactory,
      String redirectUrl) {
    this.socket = Objects.requireNonNull(socket, "socket must not be null.");
    this.tlsServerFactory =
        Objects.requireNonNull(tlsServerFactory, "tlsServerFactory must not be null.");
    this.redirectUrl = redirectUrl;
  }

  @Override
  public void run() {

    try (Socket s = socket) {
      Tap tap = new Tap(socket.getInputStream(), socket.getOutputStream());

      try (final TlsSocket tlsSocket = tlsServerFactory.bind(tap.getIn(), tap.getOut())) {

        TlsResult tlsResult = new TlsResult(s, tap);

        InputStream is = tlsSocket.getInputStream();
        HttpTransportMetricsImpl metrics = new HttpTransportMetricsImpl();
        SessionInputBufferImpl buf = new SessionInputBufferImpl(metrics, 2048);
        buf.bind(is);

        DefaultHttpRequestParser reqParser = new DefaultHttpRequestParser(buf);
        try {
          HttpRequest req = reqParser.parse();

          if (TlsConstants.REQUEST_METHOD.equals(req.getRequestLine().getMethod())
              && TlsConstants.REQUEST_URI.equals(req.getRequestLine().getUri())) {
            log.debug("Received valid TLS request");

            // add new line for easier parsing on client side
            String content = new Gson().toJson(tlsResult) + "\r\n";
            String response =
                "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: "
                    + content.length() + "\r\n\r\n" + content;

            PrintWriter writer = new PrintWriter(tlsSocket.getOutputStream());
            writer.write(response);
            writer.flush();

          } else {
            String response;
            if (redirectUrl == null) {
              response = TlsConstants.HTTP_VERSION + " 404 Not Found\r\n\r\n";
            } else {
              String path = "";
              try {
                URI uri = new URI(req.getRequestLine().getUri());
                path = uri.getPath();
              } catch (URISyntaxException e) {
                // ignore
              }

              response = TlsConstants.HTTP_VERSION + " 301 Moved Permanently\r\n" + "Location: "
                  + redirectUrl + path + "\r\n\r\n";
            }
            tlsSocket.getOutputStream().write(response.getBytes());
          }
        } catch (HttpException e) {
          e.printStackTrace();
        }

      }

    } catch (final IOException e) {
      log.error("Exception while handling client connection.", e);
    }

  }

}
