package gameshowgui.httpsController;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import gameshowgui.model.Frage;
import gameshowgui.model.Kategorie;

import org.eclipse.jetty.server.handler.AbstractHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.security.KeyStore;

public final class HttpsController{

    private static HttpsController instance;

    public static HttpsController getInstance(Kategorie[] kategorien) {
        if(instance == null) {
            instance = new HttpsController(kategorien);
        }
        return instance;
    }

    private HttpsController(Kategorie[] kategorien) {
        try {
            String alias = "obviousAlias";
            char[] password = "password1".toCharArray();
            KeyStore keystore = KeystoreUtil.createSelfSignedKeystore(alias, password);

            // SSL-Konfiguration mit In-Memory-Keystore
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStore(keystore);
            sslContextFactory.setKeyStorePassword(new String(password));
            sslContextFactory.setKeyManagerPassword(new String(password));

            // HTTPS Connector konfigurieren
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

            Server server = new Server();
            ServerConnector sslConnector = new ServerConnector(
                server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https)
            );
            sslConnector.setPort(8443);
            server.addConnector(sslConnector);

            server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target,
                               Request baseRequest,
                               HttpServletRequest request,
                               HttpServletResponse response)
                    throws IOException, ServletException {

                response.setContentType("text/plain; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                StringBuilder builder = new StringBuilder();

                for (Kategorie kat : kategorien) {
                    builder.append(kat.getName())
                            .append(",");
                    for (Frage frage : kat.getFragen()) {
                        builder.append(frage.getPunkte())
                                .append(",");
                    }
                    builder.append("\n");
                }

                response.getWriter().print(builder.toString());
                baseRequest.setHandled(true);
            }
        });

            // Server starten
            server.start();
            System.out.println("Jetty HTTPS Server läuft auf Port 8443");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
