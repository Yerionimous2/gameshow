package gameshowgui.httpsController;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class SelfSignedCertGenerator {

    public static X509Certificate generate(String dn, KeyPair keyPair, Date from, Date to, BigInteger serial) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        X500Name issuer = new X500Name(dn);
        X500Name subject = issuer;  // selbstsigniert

        // Zertifikat bauen
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                from,
                to,
                subject,
                keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(keyPair.getPrivate());

        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer));

        cert.checkValidity(new Date());
        cert.verify(keyPair.getPublic());

        return cert;
    }
}
