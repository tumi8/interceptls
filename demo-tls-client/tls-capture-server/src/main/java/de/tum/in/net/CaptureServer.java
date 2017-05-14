package de.tum.in.net;

import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CaptureServer {

    private final org.apache.logging.log4j.Logger log = LogManager.getLogger();
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final int[] ports;
    private final Future[] server;

    public CaptureServer(final int... ports) {
        this.ports = Objects.requireNonNull(ports, "ports must not be null");
        server = new Future[ports.length];
    }


    public static void main(final String[] args) {

        final CaptureServer srv = new CaptureServer(7364);
        srv.start();
    }

    private void start() throws IllegalStateException {
        for (int i = 0; i < ports.length; i++) {
            final SimpleServerSocket srvSocket = new SimpleServerSocket(ports[i], exec);
            final Future<Void> future = exec.submit(srvSocket);
            server[i] = future;
        }


        while (true) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            //search for any crashed server
            final Optional<Future> op = Arrays.stream(server).filter(s -> s.isDone()).findAny();
            if (op.isPresent()) {
                try {
                    op.get().get();
                } catch (final ExecutionException | InterruptedException e) {
                    log.error("A server terminated", e);
                    throw new IllegalStateException("A server terminated", e);
                }

            }
        }


    }
}
