package de.tum.in.net.scenario.server;

import org.bouncycastle.tls.TlsServerProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.model.TlsSocket;

/**
 * Created by johannes on 18.05.17.
 */

public class BcTlsServerFactory implements TlsServerFactory {

    private final TlsServerConfig config;

    public BcTlsServerFactory() {
        this(new DefaultTlsServerConfig());
    }

    public BcTlsServerFactory(final TlsServerConfig config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    @Override
    public TlsSocket bind(final InputStream in, final OutputStream out) throws IOException {
        final TlsServerProtocol protocol = new TlsServerProtocol(in, out);
        protocol.accept(new DefaultServer(config));
        return new TlsSocket(protocol);
    }
}
