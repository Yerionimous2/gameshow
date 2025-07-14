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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class SecondaryController {

    @FXML
    private ImageView backgroundImageView;

    @FXML
    private VBox foregroundBox; 

    @FXML
    private StackPane root;

    private Frage aktuelleFrage;
    private MediaPlayer aktuellerPlayer;

    @FXML
    private void initialize() {
        HttpsController.getInstance(this);
        Image img = new Image(DatenManager.getInstance().getDesignEinstellungen().getHintergrundbild(), true);
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage bgImage = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(bgImage));

        foregroundBox.prefWidthProperty().bind(root.widthProperty());
        foregroundBox.prefHeightProperty().bind(root.heightProperty());
    }

    public void zeigeFrage(Frage frage, Kategorie kategorie) {
        this.aktuelleFrage = frage;
        javafx.scene.control.Label frageLabel = new javafx.scene.control.Label(kategorie.getName() + " " + frage.getPunkte());
        frageLabel.setTextFill(DatenManager.getInstance().getDesignEinstellungen().getTextfarbe());
        frageLabel.setStyle("-fx-font-size: 35px;");
        frageLabel.setLayoutX(50);
        frageLabel.setLayoutY(50);
        foregroundBox.getChildren().clear();
        foregroundBox.getChildren().add(frageLabel);
        VBox.setMargin(frageLabel, new Insets(50, 0, 20, 0));
        
        StackPane fragenBox = new StackPane();
        VBox.setMargin(fragenBox, new Insets(0, 50, 50, 50));
        fragenBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(fragenBox, Priority.ALWAYS);

        fragenBox.setStyle("-fx-background-color: " + toHexString(frage.getFarbe()) + ";");
        Region frageInhalt = erstelleFrageButton(frage);
        frageInhalt.setMaxWidth(Double.MAX_VALUE);
        frageInhalt.setMaxHeight(Double.MAX_VALUE);
        StackPane.setMargin(frageInhalt, new Insets(20, 20, 20, 20));
        fragenBox.getChildren().add(frageInhalt);

        foregroundBox.getChildren().add(fragenBox);
    }

    private Region erstelleFrageButton(Frage frage) {
        VBox container = new VBox(10);
        container.setAlignment(Pos.TOP_LEFT);

        Label frageText = new Label(frage.getText());
        frageText.setTextFill(DatenManager.getInstance().getDesignEinstellungen().getTextfarbe());
        frageText.setFont(Font.font("System", FontWeight.NORMAL, 27));

        if (frage instanceof VideoFrage) {
            String link = ((VideoFrage) frage).getLink();

            Media media = new Media(link);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            mediaPlayer.setOnReady(() -> {
                mediaPlayer.seek(Duration.seconds(0));
                mediaPlayer.pause();
            });

            mediaView.setPreserveRatio(true);

            // Container-Inhalt horizontal zentrieren
            container.setAlignment(Pos.CENTER);

            container.widthProperty().addListener((obs, oldVal, newVal) -> {
                double maxWidth = newVal.doubleValue() - 100;
                if (maxWidth < 0) maxWidth = 0;
                mediaView.setFitWidth(maxWidth);
                mediaView.setVisible(maxWidth > 0 && mediaView.getFitHeight() > 0);
            });

            container.heightProperty().addListener((obs, oldVal, newVal) -> {
                double maxHeight = newVal.doubleValue() - 100;
                if (maxHeight < 0) maxHeight = 0;
                mediaView.setFitHeight(maxHeight);
                mediaView.setVisible(maxHeight > 0 && mediaView.getFitWidth() > 0);
            });

            container.getChildren().addAll(mediaView, frageText);

            aktuellerPlayer = mediaPlayer;

        } else if (frage instanceof MusikFrage) {
            String link = ((MusikFrage) frage).getLink();

            Media media = new Media(link);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            new MediaView(mediaPlayer);
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.seek(Duration.seconds(0));
                mediaPlayer.pause();
            });

            container.getChildren().addAll(frageText);

            aktuellerPlayer = mediaPlayer;

        } else if (frage instanceof FotoFrage) {
            String link = ((FotoFrage) frage).getLink();
            Image image = new Image(link);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            container.setAlignment(Pos.CENTER);

            container.widthProperty().addListener((obs, oldVal, newVal) -> {
                double maxWidth = newVal.doubleValue() - 50;
                if (maxWidth < 0) maxWidth = 0;
                imageView.setFitWidth(maxWidth);
                imageView.setVisible(maxWidth > 0 && imageView.getFitHeight() > 0);
            });

            container.heightProperty().addListener((obs, oldVal, newVal) -> {
                double maxHeight = newVal.doubleValue() - 50;
                if (maxHeight < 0) maxHeight = 0;
                imageView.setFitHeight(maxHeight);
                imageView.setVisible(maxHeight > 0 && imageView.getFitHeight() > 0);
            });

            container.getChildren().addAll(imageView, frageText);

        } else {
            container.getChildren().add(frageText);
        }

        return container;
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
        } else if(teile[0].equals("Pause")) {
            pauseMediaPlayer();
        } else if(teile[0].equals("Fortsetzen")) {
            resumeMediaPlayer();
        }
    }

    public void pauseMediaPlayer() {
        if (aktuellerPlayer != null) {
            aktuellerPlayer.pause();
        }
    }

    public void resumeMediaPlayer() {
        if (aktuellerPlayer != null) {
            aktuellerPlayer.play();
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