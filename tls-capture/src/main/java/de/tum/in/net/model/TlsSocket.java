package de.tum.in.net.model;

import org.bouncycastle.tls.TlsServerProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by johannes on 18.05.17.
 */

public class TlsSocket implements Closeable {

    private final TlsServerProtocol protocol;

    public TlsSocket(final TlsServerProtocol protocol) {
        this.protocol = protocol;
    }

    public InputStream getInputStream() {
        return protocol.getInputStream();
    }

    public OutputStream getOut() {
        return protocol.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        protocol.close();
    }
}
