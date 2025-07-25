package gameshowgui.httpsController;

import org.eclipse.jetty.server.*;

import gameshowgui.gui.ConfigController;
import gameshowgui.gui.PointsController;
import gameshowgui.gui.PrimaryController;
import gameshowgui.gui.SecondaryController;
import gameshowgui.model.DatenManager;
import gameshowgui.model.Frage;
import gameshowgui.model.Kategorie;
import gameshowgui.model.Team;

import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.application.Platform;
import jakarta.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class HttpsController {

    private static HttpsController instance;
    private static PrimaryController primaryController;
    private static SecondaryController secondaryController;
    private static PointsController pointsController;
    private static ConfigController configController;
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

    public static HttpsController getInstance(PointsController pointsController) {
        if(instance == null) {
            throw new IllegalStateException("HttpsController must be initialized with categories.");
        }
        HttpsController.pointsController = pointsController;
        return instance;
    }

    public static HttpsController getInstance(SecondaryController secondaryController) {
        if(instance == null) {
            throw new IllegalStateException("HttpsController must be initialized with categories.");
        }
        HttpsController.secondaryController = secondaryController;
        return instance;
    }

    public static HttpsController getInstance(ConfigController configController) {
        if(instance == null) {
            throw new IllegalStateException("HttpsController must be initialized with categories.");
        }
        HttpsController.configController = configController;
        return instance;
    }

    public static HttpsController getInstance() {
        if(instance == null) {
            throw new IllegalStateException("HttpsController must be initialized with categories.");
        }
        return instance;
    }

    

        private HttpsController(Kategorie[] kategorien) {
        try {
            // Server initialisieren
            Server server = new Server();

            // Normale HTTP-Verbindung auf Port 8080
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(8443);
            server.addConnector(connector);

            // Aktuelle Nachricht vorbereiten
            StringBuilder builder = new StringBuilder();
            for (Kategorie kat : kategorien) {
                builder.append(kat.getName()).append(",");
                for (Frage frage : kat.getFragen()) {
                    builder.append(frage.getPunkte()).append(",");
                }
                builder.append("\n");
            }
            builder.append("Teams:");
            for(Team team : DatenManager.getInstance().getTeams()) {
                builder.append(team.getName()).append(",");
            }
            aktuelleNachricht = builder.toString();

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
                        Platform.runLater(() -> {
                            if (primaryController != null) {
                                primaryController.handleMessage(empfangeneNachricht);
                            }
                            if (secondaryController != null) {
                                secondaryController.handleMessage(empfangeneNachricht);
                            }
                            if (pointsController != null) {
                                pointsController.handleMessage(empfangeneNachricht);
                            }
                            if (configController != null) {
                                configController.handleMessage(empfangeneNachricht);
                            }
                        });
                        response.getWriter().println("Nachricht empfangen");
                        baseRequest.setHandled(true);
                        response.setStatus(HttpServletResponse.SC_OK);
                    }

                    if ("GET".equalsIgnoreCase(request.getMethod())) {
                        PrimaryController.hideIP();
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
