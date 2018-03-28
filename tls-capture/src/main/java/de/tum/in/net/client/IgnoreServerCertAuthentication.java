package de.tum.in.net.client;

import java.io.IOException;

import org.bouncycastle.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.tls.TlsServerCertificate;

public class IgnoreServerCertAuthentication extends ServerOnlyTlsAuthentication {

  @Override
  public void notifyServerCertificate(TlsServerCertificate arg0) throws IOException {
    // ignore
  }

}
