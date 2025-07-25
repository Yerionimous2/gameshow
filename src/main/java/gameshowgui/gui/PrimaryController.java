package gameshowgui.gui;

import java.io.IOException;

import gameshowgui.httpsController.HttpsController;
import gameshowgui.model.DatenManager;
import gameshowgui.model.Frage;
import gameshowgui.model.Kategorie;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

public class PrimaryController {

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private GridPane foregroundGrid;

    @FXML
    private StackPane root;

    @FXML
    Button btnIP;

    @FXML
    private void wechselZuFrage(Frage frage, Kategorie kategorie) throws IOException {
        App.setRoot("secondary");
        
        SecondaryController secondaryController = (SecondaryController) App.getCurrentController();
        secondaryController.zeigeFrage(frage, kategorie);
    }

    public static void hideIP() {
        PrimaryController controller = (PrimaryController) App.getCurrentController();
        controller.btnIP.setVisible(false);
        DatenManager.getInstance().setHideIP();
    }



    @FXML
    private void initialize() {
        try {
            String ip = getLocalLANAddress();
            btnIP.setText("IP: " + ip + ":8443");
        } catch (Exception e) {
            btnIP.setText("IP konnte nicht ermittelt werden");
            e.printStackTrace();
        }
        if(!DatenManager.getInstance().hideIP()) {
            btnIP.setVisible(true);
        } else {
            btnIP.setVisible(false);
        }
        foregroundGrid.prefWidthProperty().bind(root.widthProperty());
        foregroundGrid.prefHeightProperty().bind(root.heightProperty());
        
        Image img;
        if(DatenManager.getInstance().getImage() == null) {
            img = new Image(DatenManager.getInstance().getDesignEinstellungen().getHintergrundbild(), true);
            DatenManager.getInstance().setImage(img);
        } else {
            img = DatenManager.getInstance().getImage();
        }
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage bgImage = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(bgImage));

        Kategorie[] kategorien = DatenManager.getInstance().getKategorien();

        try {
            HttpsController.getInstance(kategorien);
            HttpsController.getInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeGrid(kategorien);
    }

    private String getLocalLANAddress() throws Exception {
    try (var socket = new java.net.Socket()) {
        // Verbindung zu einem öffentlichen Server herstellen (nur zur IP-Ermittlung)
        socket.connect(new java.net.InetSocketAddress("8.8.8.8", 53), 1000);
        return socket.getLocalAddress().getHostAddress();
    }
}

    @FXML
    private void onHideIP() {
        DatenManager.getInstance().setHideIP();
        btnIP.setVisible(false);
    }

    private void initializeGrid(Kategorie[] kategorien) {
        foregroundGrid.getChildren().clear();
        int row = 0;
        int column = 0;
        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        foregroundGrid.getRowConstraints().add(0, row0);
        for (Kategorie kategorie : kategorien) {
            if(kategorie.getName().equals("Anpassungen")) {
                continue;
            }
            Label kategorieLabel = new javafx.scene.control.Label(kategorie.getName());
            foregroundGrid.add(kategorieLabel, column, 0);
            kategorieLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            kategorieLabel.setAlignment(Pos.CENTER);
            GridPane.setHgrow(kategorieLabel, Priority.ALWAYS);
            GridPane.setVgrow(kategorieLabel, Priority.ALWAYS);
            GridPane.setHalignment(kategorieLabel, HPos.CENTER);
            kategorieLabel.setTextFill(DatenManager.getInstance().getDesignEinstellungen().getTextfarbe());
            kategorieLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
            row++;
            kategorie.sort();
            for (Frage frage : kategorie.getFragen()) {
                FragenPanel frageButton = new FragenPanel(frage);
                frageButton.setOnAction(event -> {
                    try {
                        HttpsController.getInstance().sendMessage("Frage," + kategorie.getName() + "," + frage.getPunkte());
                        wechselZuFrage(frage, kategorie);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                frageButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(frageButton, Priority.ALWAYS);
                GridPane.setVgrow(frageButton, Priority.ALWAYS);
                frageButton.setTextFill(DatenManager.getInstance().getDesignEinstellungen().getTextfarbe());
                frageButton.setStyle("-fx-background-color: " + toHexString(frage.getFarbe()) + ";");
                foregroundGrid.add(frageButton, column, row);
                row++;
            }
            column++;
            row = 0;
        }
    }

    private String toHexString(Color farbe) {
        int r = (int) Math.round(farbe.getRed() * 255);
        int g = (int) Math.round(farbe.getGreen() * 255);
        int b = (int) Math.round(farbe.getBlue() * 255);
        int o = (int) Math.round(farbe.getOpacity() * 255);
        return String.format("#%02X%02X%02X%02X", r, g, b, o);
    }

    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Frage")) {
            Frage frage = DatenManager.getInstance().findeFrage(teile[1], Integer.parseInt(teile[2]));
            Kategorie kategorie = DatenManager.getInstance().findeKategorie(frage);
            try {
                wechselZuFrage(frage, kategorie);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(teile[0].equals("Punkte")) {
            try {
                App.setRoot("points");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(teile[0].equals("Anpassen")) {
            if(DatenManager.getInstance().findeKategorie("Anpassungen") == null) {
                Kategorie anpassungen = new Kategorie("Anpassungen", new Frage[0]);
                Kategorie[] kategorienOrig = DatenManager.getInstance().getKategorien();
                Kategorie[] kategorienNeu = new Kategorie[kategorienOrig.length + 1];
                System.arraycopy(kategorienOrig, 0, kategorienNeu, 0, kategorienOrig.length);
                kategorienNeu[kategorienOrig.length] = anpassungen;
                DatenManager.getInstance().setKategorien(kategorienNeu);
                DatenManager.getInstance().speichern();
            }
            Kategorie anpassungenOrig = DatenManager.getInstance().findeKategorie("Anpassungen");
            Frage[] fragenOrig = anpassungenOrig.getFragen();
            Frage[] fragenNeu = new Frage[fragenOrig.length + 1];
            System.arraycopy(fragenOrig, 0, fragenNeu, 0, fragenOrig.length);
            Frage addFrage = new Frage(Integer.parseInt(teile[2]),"");
            addFrage.setTeam(DatenManager.getInstance().findeTeam(teile[1]));
            fragenNeu[fragenOrig.length] = addFrage;
            anpassungenOrig.setFragen(fragenNeu);
            DatenManager.getInstance().speichern();
        } else if(teile[0].equals("Zurücksetzen")) {
            DatenManager.getInstance().zurücksetzen();
            try {
                App.setRoot("secondary");
                App.setRoot("primary");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(teile[0].equals("Anpassungsmodus")) {
            try {
                App.setRoot("config");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}