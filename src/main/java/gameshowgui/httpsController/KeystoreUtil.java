package gameshowgui.httpsController;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

public class KeystoreUtil {

    public static KeyStore createSelfSignedKeystore(String alias, char[] password) throws Exception {
        // Schlüsselpaar erzeugen
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // Selbstsigniertes Zertifikat erzeugen
        long now = System.currentTimeMillis();
        Date from = new Date(now);
        Date to = new Date(now + (365L * 24 * 60 * 60 * 1000)); // 1 Jahr gültig
        BigInteger serial = new BigInteger(64, new SecureRandom());

        X509Certificate cert = SelfSignedCertGenerator.generate("CN=localhost", keyPair, from, to, serial);

        // Keystore im Speicher
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null); // leerer Keystore
        ks.setKeyEntry(alias, keyPair.getPrivate(), password, new java.security.cert.Certificate[]{cert});

        return ks;
    }
}