package gameshowgui.gui;

import java.io.IOException;

import gameshowgui.httpsController.HttpsController;
import gameshowgui.model.Frage;
import gameshowgui.model.Kategorie;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
    private void wechselZuFrage(Frage frage) throws IOException {
        // TODO: Implement logic to handle the question
        App.setRoot("secondary");
    }

    @FXML
    private void initialize() {
        root.widthProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
        root.heightProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
        foregroundGrid.prefWidthProperty().bind(root.widthProperty());
        foregroundGrid.prefHeightProperty().bind(root.heightProperty());
        //TODO: Read the background image from the config file
        backgroundImageView.setImage(new Image(getClass().getResource("/images/background.jpg").toExternalForm()));

        //TODO: Read the Categories and Questions from the config file
        Kategorie[] kategorien = {
            new Kategorie("Kategorie 1", new Frage[]{
                new Frage(100, "Frage 1.1"),
                new Frage(200, "Frage 1.2"),
                new Frage(300, "Frage 1.3"),
                new Frage(400, "Frage 1.4"),
                new Frage(500, "Frage 1.5")
            }),
            new Kategorie("Kategorie 2", new Frage[]{
                new Frage(100, "Frage 2.1"),
                new Frage(200, "Frage 2.2"),
                new Frage(300, "Frage 2.3"),
                new Frage(400, "Frage 2.4"),
                new Frage(500, "Frage 2.5")
            }),
            new Kategorie("Kategorie 3", new Frage[]{
                new Frage(100, "Frage 3.1"),
                new Frage(200, "Frage 3.2"),
                new Frage(300, "Frage 3.3"),
                new Frage(400, "Frage 3.4"),
                new Frage(500, "Frage 3.5")
            }),
            new Kategorie("Kategorie 4", new Frage[]{
                new Frage(100, "Frage 4.1"),
                new Frage(200, "Frage 4.2"),
                new Frage(300, "Frage 4.3"),
                new Frage(400, "Frage 4.4"),
                new Frage(500, "Frage 4.5")
            }),
            new Kategorie("Kategorie 5", new Frage[]{
                new Frage(100, "Frage 5.1"),
                new Frage(200, "Frage 5.2"),
                new Frage(300, "Frage 5.3"),
                new Frage(400, "Frage 5.4"),
                new Frage(500, "Frage 5.5")
            }),
            new Kategorie("Kategorie 6", new Frage[]{
                new Frage(100, "Frage 6.1"),
                new Frage(200, "Frage 6.2"),
                new Frage(300, "Frage 6.3"),
                new Frage(400, "Frage 6.4"),
                new Frage(500, "Frage 6.5")
            })
        };

        try {
            HttpsController.getInstance(kategorien);
            HttpsController.getInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeGrid(kategorien);
    }

    private void initializeGrid(Kategorie[] kategorien) {
        foregroundGrid.getChildren().clear();
        int row = 0;
        int column = 0;
        RowConstraints row0 = new RowConstraints();
        row0.setVgrow(Priority.ALWAYS);
        foregroundGrid.getRowConstraints().add(0, row0);
        for (Kategorie kategorie : kategorien) {
            Label kategorieLabel = new javafx.scene.control.Label(kategorie.getName());
            foregroundGrid.add(kategorieLabel, column, 0);
            kategorieLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            kategorieLabel.setAlignment(Pos.CENTER);
            GridPane.setHgrow(kategorieLabel, Priority.ALWAYS);
            GridPane.setVgrow(kategorieLabel, Priority.ALWAYS);
            GridPane.setHalignment(kategorieLabel, HPos.CENTER);
            // TODO: Read the color from the config file
            kategorieLabel.setStyle("-fx-font-weight: bold;-fx-text-fill: white; -fx-font-size: 20px;");
            row++;
            kategorie.sort();
            for (Frage frage : kategorie.getFragen()) {
                FragenPanel frageButton = new FragenPanel(frage);
                frageButton.setOnAction(event -> {
                    try {
                        HttpsController.getInstance().sendMessage("Frage," + kategorie.getName() + "," + frage.getPunkte());
                        wechselZuFrage(frage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                frageButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(frageButton, Priority.ALWAYS);
                GridPane.setVgrow(frageButton, Priority.ALWAYS);
                // TODO: Read the color from the config file
                frageButton.setStyle("-fx-background-color: " + toHexString(frage.getFarbe()) + ";-fx-text-fill: white;");
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

    private void adjustBackgroundSize() {
        double paneRatio = root.getWidth() / root.getHeight();
        double imageRatio = backgroundImageView.getImage().getWidth() / backgroundImageView.getImage().getHeight();

        if (paneRatio > imageRatio) {
            backgroundImageView.setFitWidth(root.getWidth());
            backgroundImageView.setFitHeight(-1);
     } else {
           backgroundImageView.setFitHeight(root.getHeight());
           backgroundImageView.setFitWidth(-1);
        }
    }

    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Frage")) {
            // TODO: Wechsel zu secondary mit der Frage
        } else if(teile[0].equals("Punkte")) {
            // TODO: Wechsel zur Punkteanzeige
        } else if(teile[0].equals("Anpassen")) {
            // TODO: Die Punkte von Team teile[1] um teile[2] anpassen
        } else if(teile[0].equals("Zurücksetzen")) {
            // TODO: Alle Teams aus allen Fragen entfernen
        }
    }
}