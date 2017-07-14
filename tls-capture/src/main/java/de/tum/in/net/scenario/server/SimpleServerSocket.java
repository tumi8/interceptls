package de.tum.in.net.scenario.server;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.ClientHandlerFactory;

/**
 * Creates a server socket. New connections will be handled by ClientHandlerFactory and subsequently
 * executed on the ExecutorService. Created by johannes on 14.05.17.
 */
public class SimpleServerSocket implements Runnable, Closeable {

  private final Logger log = LoggerFactory.getLogger(SimpleServerSocket.class);
  private final ExecutorService exec;
  private final int port;
  private final ClientHandlerFactory clientHandlerFactory;
  private ServerSocket srv;

  public SimpleServerSocket(final int port, final ClientHandlerFactory clientHandlerFactory,
      final ExecutorService exec) {
    this.port = port;
    this.clientHandlerFactory = clientHandlerFactory;
    this.exec = exec;
  }

  @Override
  public void run() {
    log.debug("Open server socket on port {}", port);

    // use try with resource block so that
    try (ServerSocket srv = new ServerSocket(port)) {
      this.srv = srv;

      log.info("Server running on port {}", port);

      while (true) {
        // blocks
        final Socket socket = srv.accept();
        log.info("New client connection {}", socket.getRemoteSocketAddress());
        final Runnable clientHandler = clientHandlerFactory.createClientHandler(socket);
        exec.submit(clientHandler);
      }
    } catch (final IOException e) {
      log.error("Unexpected IOException", e);
    } finally {
      log.warn("Server stopped on port {}.", port);
    }

  }

  public boolean isRunning() {
    return srv == null ? false : !srv.isClosed();
  }

  public int getLocalPort() {
    return srv.getLocalPort();
  }

  @Override
  public void close() throws IOException {
    if (srv == null)
      throw new IllegalStateException("The run method must be executed first.");
    srv.close();
  }
}
