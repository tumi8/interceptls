package de.tum.in.net;

import org.bouncycastle.crypto.tls.TlsServerProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

import static org.junit.Assert.fail;

/**
 * Created by johannes on 31.03.17.
 */

public class ServerRunnable implements Runnable {

    private final int port;
    private byte[] buffer;

    public ServerRunnable(int port, int bytesToRead) {
        this.port = port;
        this.buffer = new byte[bytesToRead];
    }

    public byte[] getReceivedBytes() {
        return buffer;
    }


    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            Socket s = serverSocket.accept();

            TlsServerProtocol protocol = new TlsServerProtocol(s.getInputStream(), s.getOutputStream(), new SecureRandom());
            protocol.accept(new DefaultServer());

            protocol.getInputStream().read(buffer);

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }


    }
}
