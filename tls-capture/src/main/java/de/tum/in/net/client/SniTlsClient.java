package de.tum.in.net.client;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Vector;

import org.bouncycastle.tls.DefaultTlsClient;
import org.bouncycastle.tls.NameType;
import org.bouncycastle.tls.ServerName;
import org.bouncycastle.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.tls.TlsAuthentication;
import org.bouncycastle.tls.TlsServerCertificate;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SniTlsClient extends DefaultTlsClient {

  private static final Logger log = LoggerFactory.getLogger(TrimmedTlsClient.class);
  private static final TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());
  private final String sni;

  public SniTlsClient(String sni) {
    super(crypto);
    this.sni = Objects.requireNonNull(sni);
  }

  @Override
  public TlsAuthentication getAuthentication() throws IOException {
    return new ServerOnlyTlsAuthentication() {
      @Override
      public void notifyServerCertificate(final TlsServerCertificate serverCertificate)
          throws IOException {
        log.debug("Notify server certificate.");
      }
    };
  }

  @Override
  protected Vector<?> getSNIServerNames() {
    final ServerName sn = new ServerName(NameType.host_name, sni);
    final Vector<ServerName> vlist = new Vector<>(1);
    vlist.add(sn);
    return vlist;
  }


}
