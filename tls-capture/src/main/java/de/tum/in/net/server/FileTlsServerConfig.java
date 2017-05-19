package de.tum.in.net.server;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.tls.AlertDescription;
import org.bouncycastle.tls.CipherSuite;
import org.bouncycastle.tls.HashAlgorithm;
import org.bouncycastle.tls.SignatureAlgorithm;
import org.bouncycastle.tls.SignatureAndHashAlgorithm;
import org.bouncycastle.tls.TlsCredentialedSigner;
import org.bouncycastle.tls.TlsFatalAlert;
import org.bouncycastle.tls.TlsServerContext;
import org.bouncycastle.tls.crypto.TlsCertificate;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.TlsCryptoParameters;
import org.bouncycastle.tls.crypto.impl.bc.BcDefaultTlsCredentialedSigner;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCertificate;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import de.tum.in.net.scenario.server.TlsServerConfig;

/**
 * Created by johannes on 19.05.17.
 */

public class FileTlsServerConfig implements TlsServerConfig {
    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private final BcTlsCrypto crypto = new BcTlsCrypto(new SecureRandom());
    private final SignatureAndHashAlgorithm sigAndHashAlg;
    private final AsymmetricKeyParameter privateKey;
    private final org.bouncycastle.tls.Certificate cert;
    private final int[] cipherSuites;

    public FileTlsServerConfig(final File certFile, final File keyFile) throws IOException, CertificateException {

        try (final InputStream in = new FileInputStream(certFile)) {
            final CertificateFactory factory = CertificateFactory.getInstance("X.509");
            final Certificate cert = factory.generateCertificate(in);

            final TlsCertificate[] certs = {new BcTlsCertificate(crypto, cert.getEncoded())};
            this.cert = new org.bouncycastle.tls.Certificate(certs);

        }

        final short signatureAlg;
        try (final PEMParser pp = new PEMParser(new FileReader(keyFile))) {
            final PEMKeyPair pemKeyPair = (PEMKeyPair) pp.readObject();
            this.privateKey = PrivateKeyFactory.createKey(pemKeyPair.getPrivateKeyInfo());

            if (this.privateKey instanceof RSAKeyParameters) {
                signatureAlg = SignatureAlgorithm.rsa;
                this.cipherSuites = new int[]{
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384

                };
            } else if (this.privateKey instanceof ECKeyParameters) {
                signatureAlg = SignatureAlgorithm.ecdsa;
                this.cipherSuites = new int[]{
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384
                };
            } else {
                throw new IllegalStateException("Unknown key type: " + this.privateKey);
            }
        }

        this.sigAndHashAlg = new SignatureAndHashAlgorithm(HashAlgorithm.sha256, signatureAlg);

    }

    @Override
    public TlsCrypto getCrypto() {
        return crypto;
    }

    @Override
    public TlsCredentialedSigner getRSASignerCredentials(final TlsServerContext context) throws IOException {
        assertSignatureAlgorithm(SignatureAlgorithm.rsa);
        return new BcDefaultTlsCredentialedSigner(new TlsCryptoParameters(context), crypto, privateKey, cert, sigAndHashAlg);
    }


    private void assertSignatureAlgorithm(final short alg) throws IOException {
        if (this.sigAndHashAlg.getSignature() != alg) {
            throw new TlsFatalAlert(AlertDescription.internal_error);
        }
    }

    @Override
    public TlsCredentialedSigner getECDSASignerCredentials(final TlsServerContext context) throws IOException {
        assertSignatureAlgorithm(SignatureAlgorithm.ecdsa);
        return new BcDefaultTlsCredentialedSigner(new TlsCryptoParameters(context), crypto, privateKey, cert, sigAndHashAlg);
    }

    @Override
    public int[] getCipherSuites() {
        return cipherSuites.clone();
    }
}
