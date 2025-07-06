package gameshowgui.gui;

import java.io.IOException;

import gameshowgui.httpsController.HttpsController;
import gameshowgui.model.DatenManager;
import gameshowgui.model.FotoFrage;
import gameshowgui.model.Frage;
import gameshowgui.model.Kategorie;
import gameshowgui.model.MusikFrage;
import gameshowgui.model.VideoFrage;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SecondaryController {

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private VBox foregroundBox; 

    @FXML
    private StackPane root;

    private Frage aktuelleFrage;

    @FXML
    private void initialize() {
        HttpsController.getInstance(this);
        // TODO: Read the background image from the config file
        backgroundImageView.setImage(new Image(getClass().getResource("/images/background.jpg").toExternalForm()));

        root.widthProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
        root.heightProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());

        foregroundBox.prefWidthProperty().bind(root.widthProperty());
        foregroundBox.prefHeightProperty().bind(root.heightProperty());
    }

    private void adjustBackgroundSize() {
        if (backgroundImageView.getImage() == null) return;

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

    public void zeigeFrage(Frage frage, Kategorie kategorie) {
        this.aktuelleFrage = frage;
        javafx.scene.control.Label frageLabel = new javafx.scene.control.Label(kategorie.getName() + " " + frage.getPunkte());
        frageLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        frageLabel.setLayoutX(50);
        frageLabel.setLayoutY(50);
        foregroundBox.getChildren().clear();
        foregroundBox.getChildren().add(frageLabel);
        VBox.setMargin(frageLabel, new Insets(50, 0, 20, 0));
        
        StackPane fragenBox = new StackPane();
        VBox.setMargin(fragenBox, new Insets(0, 50, 50, 50));
        fragenBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(fragenBox, Priority.ALWAYS);

        fragenBox.setStyle("-fx-background-color: " + toHexString(frage.getFarbe()) + ";-fx-text-fill: white;");
        Region frageInhalt = erstelleFrageButton(frage);
        frageInhalt.setMaxWidth(Double.MAX_VALUE);
        frageInhalt.setMaxHeight(Double.MAX_VALUE);
        StackPane.setMargin(frageInhalt, new Insets(20, 20, 20, 20));
        fragenBox.getChildren().add(frageInhalt);

        foregroundBox.getChildren().add(fragenBox);
    }

    private Region erstelleFrageButton(Frage frage) {
        if (frage instanceof VideoFrage) {
            // TODO: handle VideoFrage-specific logic here
        }
        if (frage instanceof MusikFrage) {
            // TODO: handle MusikFrage-specific logic here
        }
        if (frage instanceof FotoFrage) {
            // TODO: handle FotoFrage-specific logic here
        }
        Label result = new Label(frage.getText());
        result.setAlignment(Pos.TOP_LEFT);
        return result;
    }

    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Team")) {
            if(teile[1].equals("löschen")) {
                aktuelleFrage.setTeam(null);
            } else {
                aktuelleFrage.setTeam(DatenManager.getInstance().findeTeam(teile[1]));
            }
            DatenManager.getInstance().speichern();
            try {
                wechselZuÜbersicht();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(teile[0].equals("Zurück")) {
            try {
                wechselZuÜbersicht();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void wechselZuÜbersicht() throws IOException {
        App.setRoot("primary");
    }

    private String toHexString(Color farbe) {
        int r = (int) Math.round(farbe.getRed() * 255);
        int g = (int) Math.round(farbe.getGreen() * 255);
        int b = (int) Math.round(farbe.getBlue() * 255);
        int o = (int) Math.round(farbe.getOpacity() * 255);
        return String.format("#%02X%02X%02X%02X", r, g, b, o);
    }
}