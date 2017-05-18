package de.tum.in.net.model;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by johannes on 18.05.17.
 */

public class TlsSocket {

    private final InputStream in;
    private final OutputStream out;

    public TlsSocket(final InputStream in, final OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public InputStream getInputStream() {
        return this.in;
    }

    public OutputStream getOut() {
        return this.out;
    }
}
