package de.tum.in.net;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.bc.BcX509v3CertificateBuilder;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.crypto.tls.DefaultTlsServer;
import org.bouncycastle.crypto.tls.DefaultTlsSignerCredentials;
import org.bouncycastle.crypto.tls.TlsSignerCredentials;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.joda.time.DateTime;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Server which creates a RSA KeyPair and a corresponding X509Certificate which is served to the clients.
 * Created by johannes on 31.03.17.
 */
public class DefaultServer extends DefaultTlsServer {

    private Certificate certificate;
    private AsymmetricKeyParameter privateKey;

    public DefaultServer() {

        try {
            final KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            final KeyPair pair = gen.generateKeyPair();
            this.privateKey = PrivateKeyFactory.createKey(pair.getPrivate().getEncoded());

            final X500Name issuer = new X500Name("CN=TUM App, O=Technische Universitaet Muenchen, C=DE");
            final X500Name subject = new X500Name("CN=TUM App, O=Technische Universitaet Muenchen, C=DE");

            final Date notBefore = DateTime.now().minusDays(7).toDate();
            final Date notAfter = DateTime.now().plusDays(7).toDate();

            final AsymmetricKeyParameter publicKey = PublicKeyFactory.createKey(pair.getPublic().getEncoded());

            final BigInteger serial = BigInteger.valueOf(1);
            final BcX509v3CertificateBuilder certBuilder = new BcX509v3CertificateBuilder(issuer, serial, notBefore, notAfter, subject, publicKey);


            final AlgorithmIdentifier sigAlg = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA256withRSA");
            final AlgorithmIdentifier digAlg = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlg);
            final BcRSAContentSignerBuilder builder = new BcRSAContentSignerBuilder(sigAlg, digAlg);
            final ContentSigner signer = builder.build(privateKey);
            final X509CertificateHolder holder = certBuilder.build(signer);

            this.certificate = new Certificate(new org.bouncycastle.asn1.x509.Certificate[]{
                    holder.toASN1Structure()});
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Java must support RSA.");
        } catch (final IOException e) {
            e.printStackTrace();
        } catch (final OperatorCreationException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected TlsSignerCredentials getRSASignerCredentials()
            throws IOException {
        return new DefaultTlsSignerCredentials(context, certificate, privateKey);
    }

}
