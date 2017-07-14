package de.tum.in.net.scenario.client;

import java.io.IOException;
import java.net.Socket;
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
import java.util.Objects;
import java.util.Set;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.ServerOnlyTlsAuthentication;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.tls.AlertDescription;
import org.bouncycastle.tls.TlsFatalAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.net.model.Tap;
import de.tum.in.net.model.TestID;
import de.tum.in.net.scenario.Node;
import de.tum.in.net.scenario.Scenario;
import de.tum.in.net.scenario.ScenarioResult;
import de.tum.in.net.scenario.ScenarioResult.ScenarioResultBuilder;

/**
 * Created by johannes on 31.05.17.
 */
public class CaClientScenario implements Scenario {

  private static final Logger log = LoggerFactory.getLogger(CaClientScenario.class);
  private static final JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
  private final String destination;
  private final int port;
  private Set<TrustAnchor> trustAnchors;
  private TestID testId;


  public CaClientScenario(TestID testId, final String destination, final int port,
      Set<TrustAnchor> trustAnchors) {
    this.testId = testId;
    this.destination = Objects.requireNonNull(destination, "destination must not be null.");
    this.port = port;
    this.trustAnchors = trustAnchors;
  }

  @Override
  public ScenarioResult call() {
    ScenarioResult result;
    Tap tap = null;
    log.debug("Trying to connect to {}:{}", destination, port);
    try (Socket s = new Socket(destination, port)) {
      tap = new Tap(s.getInputStream(), s.getOutputStream());

      // connect in blocking mode
      final TlsClientProtocol tlsClientProtocol =
          new TlsClientProtocol(tap.getIn(), tap.getOut(), new SecureRandom());
      tlsClientProtocol.connect(new DefaultTlsClient() {
        @Override
        public TlsAuthentication getAuthentication() throws IOException {
          return new ServerOnlyTlsAuthentication() {
            @Override
            public void notifyServerCertificate(final Certificate serverCertificate)
                throws IOException {
              log.debug("Notify server certificate.");
              org.bouncycastle.asn1.x509.Certificate[] certs =
                  serverCertificate.getCertificateList();

              List<X509Certificate> x509 = new ArrayList<>();
              for (org.bouncycastle.asn1.x509.Certificate cert : certs) {
                try {
                  x509.add(conv.getCertificate(new X509CertificateHolder(cert)));
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

      // we are now connected, therefore we can publish the captured bytes
      result = new ScenarioResultBuilder(Node.CLIENT, s).sent(tap.getOutputytes())
          .received(tap.getInputBytes()).connected();

      // then we send our session-id
      tlsClientProtocol.getOutputStream().write(testId.getTransmitBytes());

      tlsClientProtocol.close();

    } catch (final IOException e) {
      log.warn("Error in " + toString(), e);

      result =
          new ScenarioResultBuilder(Node.CLIENT, "Client", destination).transmitted(tap).error(e);
    }

    return result;

  }

  @Override
  public String toString() {
    return CaClientScenario.class.getName();
  }

  @Override
  public TestID getTestID() {
    return testId;
  }
}
