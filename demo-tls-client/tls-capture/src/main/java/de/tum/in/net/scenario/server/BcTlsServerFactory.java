package de.tum.in.net.scenario.server;

import org.bouncycastle.tls.TlsServerProtocol;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;

import de.tum.in.net.model.TlsServerFactory;
import de.tum.in.net.model.TlsSocket;

/**
 * Created by johannes on 18.05.17.
 */

public class BcTlsServerFactory implements TlsServerFactory {

    private final BcTlsCrypto crypto = new BcTlsCrypto(new SecureRandom());

    @Override
    public TlsSocket bind(final InputStream in, final OutputStream out) throws IOException {
        final TlsServerProtocol protocol = new TlsServerProtocol(in, out);
        protocol.accept(new DefaultServer(crypto));
        return new TlsSocket(protocol.getInputStream(), protocol.getOutputStream());
    }
}
