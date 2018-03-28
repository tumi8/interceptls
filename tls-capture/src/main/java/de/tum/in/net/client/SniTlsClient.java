package de.tum.in.net.client;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import org.bouncycastle.tls.NameType;
import org.bouncycastle.tls.ServerName;
import org.bouncycastle.tls.TlsAuthentication;

public class SniTlsClient extends TlsDetectionClient {

  private final String sni;

  public SniTlsClient(String sni) {
    this.sni = Objects.requireNonNull(sni);
  }

  @Override
  public TlsAuthentication getAuthentication() throws IOException {
    return new IgnoreServerCertAuthentication();
  }

  @Override
  protected Vector<?> getSNIServerNames() {
    final ServerName sn = new ServerName(NameType.host_name, sni);
    final Vector<ServerName> vlist = new Vector<>(1);
    vlist.add(sn);
    return vlist;
  }


}
