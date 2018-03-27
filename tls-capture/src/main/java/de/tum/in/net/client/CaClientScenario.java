package de.tum.in.net.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bouncycastle.tls.AlertDescription;
import org.bouncycastle.tls.DefaultTlsClient;
import org.bouncycastle.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.tls.TlsAuthentication;
import org.bouncycastle.tls.TlsFatalAlert;
import org.bouncycastle.tls.TlsServerCertificate;
import org.bouncycastle.tls.crypto.TlsCertificate;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by johannes on 31.05.17.
 */
public class CaClientScenario extends DefaultHttpsScenario {

  private static final Logger log = LoggerFactory.getLogger(CaClientScenario.class);
  private static final TlsCrypto crypto = new BcTlsCrypto(new SecureRandom());

  public CaClientScenario(HostAndPort target, final Set<TrustAnchor> trustAnchors) {
    super(target, new DefaultTlsClient(crypto) {

      @Override
      public TlsAuthentication getAuthentication() throws IOException {
        return new ServerOnlyTlsAuthentication() {

          @Override
          public void notifyServerCertificate(TlsServerCertificate serverCertificate)
              throws IOException {
            log.debug("Notify server certificate.");
            TlsCertificate[] certs = serverCertificate.getCertificate().getCertificateList();

            List<X509Certificate> x509 = new ArrayList<>();
            for (TlsCertificate cert : certs) {
              try {
                X509Certificate jsCert = (X509Certificate) CertificateFactory.getInstance("X.509")
                    .generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
                x509.add(jsCert);
              } catch (CertificateException e) {
                throw new TlsFatalAlert(AlertDescription.bad_certificate, e);
              }
            }

            try {
              CertificateFactory fac = CertificateFactory.getInstance("X.509", "BC");
              CertPath cp = fac.generateCertPath(x509);

              PKIXParameters pkixp = new PKIXParameters(trustAnchors);
              pkixp.setRevocationEnabled(false);

              CertPathValidator cpv = CertPathValidator.getInstance("PKIX", "BC");
              cpv.validate(cp, pkixp);

            } catch (NoSuchAlgorithmException | CertificateException
                | InvalidAlgorithmParameterException | CertPathValidatorException
                | NoSuchProviderException e) {
              log.error("Bad certificate", e);
              throw new TlsFatalAlert(AlertDescription.bad_certificate, e);
            }
          }
        };
      }
    });
  }

  @Override
  public String toString() {
    return CaClientScenario.class.getName();
  }

}
