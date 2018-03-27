package de.tum.in.net.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tum.in.net.model.TlsSocket;

/**
 * Created by johannes on 18.05.17.
 */

public interface TlsServerFactory {

  TlsSocket bind(InputStream in, OutputStream out) throws IOException;
}
