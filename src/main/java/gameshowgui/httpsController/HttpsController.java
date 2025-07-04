package gameshowgui.httpsController;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import gameshowgui.gui.PrimaryController;
import gameshowgui.gui.SecondaryController;
import gameshowgui.model.Frage;
import gameshowgui.model.Kategorie;

import org.eclipse.jetty.server.handler.AbstractHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class HttpsController{

    private static HttpsController instance;
    private static PrimaryController primaryController;
    private static SecondaryController secondaryController;
    private Set<HttpServletResponse> clients = ConcurrentHashMap.newKeySet();
    private String aktuelleNachricht;

    public static HttpsController getInstance(Kategorie[] kategorien) {
        if(instance == null) {
            instance = new HttpsController(kategorien);
        }
        return instance;
    }

    public static HttpsController getInstance(PrimaryController primaryController) {
        if(instance == null) {
            throw new IllegalStateException("HttpsController must be initialized with categories.");
        }
        HttpsController.primaryController = primaryController;
        return instance;
    }

    public static HttpsController getInstance(SecondaryController secondaryController) {
        if(instance == null) {
            throw new IllegalStateException("HttpsController must be initialized with categories.");
        }
        HttpsController.secondaryController = secondaryController;
        return instance;
    }

    private HttpsController(Kategorie[] kategorien) {
        try {
            String alias = "obviousAlias";
            char[] password = "password1".toCharArray();
            KeyStore keystore = KeystoreUtil.createSelfSignedKeystore(alias, password);

            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStore(keystore);
            sslContextFactory.setKeyStorePassword(new String(password));
            sslContextFactory.setKeyManagerPassword(new String(password));

            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

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
                aktuelleNachricht = builder.toString();

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

                if ("POST".equalsIgnoreCase(request.getMethod())) {
                    String empfangeneNachricht;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
                        empfangeneNachricht = reader.lines().collect(Collectors.joining("\n"));
                    }
                    if (primaryController != null) {
                        primaryController.handleMessage(empfangeneNachricht);
                        response.getWriter().println("Nachricht empfangen");
                        baseRequest.setHandled(true);
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else if (secondaryController != null) {
                        secondaryController.handleMessage(empfangeneNachricht);
                        response.getWriter().println("Nachricht empfangen");
                        baseRequest.setHandled(true);
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.getWriter().println("Keine Controller vorhanden, Nachricht nicht verarbeitet");
                        baseRequest.setHandled(true);
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                }

                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    clients.add(response);
                    response.setContentType("text/plain; charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().println(aktuelleNachricht);
                    response.getWriter().flush();
                    baseRequest.setHandled(true);
                }
            }
        });
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendMessage(String message) {
        this.aktuelleNachricht = message;
        synchronized (clients) {
            for (HttpServletResponse client : clients) {
                try {
                    client.getWriter().println(message);
                    client.getWriter().flush();
                } catch (IOException e) {
                }
            }
        }
    }
}
