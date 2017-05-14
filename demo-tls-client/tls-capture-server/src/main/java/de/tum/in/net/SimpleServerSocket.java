package de.tum.in.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created by johannes on 14.05.17.
 */

public class SimpleServerSocket implements Callable<Void> {

    private final Logger log = LogManager.getLogger();
    private final int port;
    private final ExecutorService exec;

    public SimpleServerSocket(final int port, final ExecutorService executor) {
        this.port = port;
        this.exec = executor;
    }

    @Override
    public Void call() throws Exception {
        final ServerSocket srv = new ServerSocket(port);
        log.info("Server running on port {}", port);

        while (true) {
            //blocks
            final Socket socket = srv.accept();
            log.debug("Received new client connection: {}", socket.getRemoteSocketAddress());
            exec.submit(new ClientConnection(socket));
        }


    }
}
