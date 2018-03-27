package de.tum.in.net.server;

import org.bouncycastle.tls.TlsCredentialedSigner;
import org.bouncycastle.tls.TlsServerContext;
import org.bouncycastle.tls.crypto.TlsCrypto;

import java.io.IOException;

/**
 * Created by johannes on 19.05.17.
 */

public interface TlsServerConfig {

  TlsCrypto getCrypto();

  TlsCredentialedSigner getRSASignerCredentials(TlsServerContext context) throws IOException;

  TlsCredentialedSigner getECDSASignerCredentials(TlsServerContext context) throws IOException;

  int[] getCipherSuites();
}
