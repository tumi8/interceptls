package de.tum.in.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tum.in.net.model.ClientHandlerFactory;
import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.scenario.server.BcTlsServerFactory;
import de.tum.in.net.scenario.server.DefaultClientHandlerFactory;
import de.tum.in.net.scenario.server.SimpleServerSocket;

public class CaptureServer {

    private final Logger log = LoggerFactory.getLogger(CaptureServer.class);
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private final Thread[] serverThreads;
    private final int[] ports;
    private final SimpleServerSocket[] server;
    private boolean started = false;
    private final TlsServerFactory prov = new BcTlsServerFactory();

    private final ClientHandlerFactory handler = new DefaultClientHandlerFactory(prov, (result) -> {
        //TODO define result listener
        System.err.println("TODO");

    });

    public CaptureServer(final int... ports) {
        this.ports = Objects.requireNonNull(ports, "ports must not be null");
        server = new SimpleServerSocket[ports.length];
        serverThreads = new Thread[ports.length];
    }

    public static void main(final String[] args) {
        final CaptureServer srv = new CaptureServer(7364);
        srv.start();
    }

    public synchronized void start() throws IllegalStateException {
        started = true;
        for (int i = 0; i < ports.length; i++) {
            final SimpleServerSocket srvSocket = new SimpleServerSocket(ports[i], handler, exec);
            server[i] = srvSocket;
            serverThreads[i] = new Thread(srvSocket, "Server Port " + ports[i]);
            serverThreads[i].start();
        }
    }

    public boolean isRunning() {
        for (final Thread t : serverThreads) {
            if (t.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public synchronized void stop() throws IOException {
        for (final SimpleServerSocket t : server) {
            t.stop();
        }
        exec.shutdown();
    }


}
