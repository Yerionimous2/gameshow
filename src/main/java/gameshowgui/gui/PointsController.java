package gameshowgui.gui;

import java.io.IOException;

import gameshowgui.httpsController.HttpsController;
import gameshowgui.model.DatenManager;
import gameshowgui.model.Team;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PointsController {
    @FXML
    private ImageView backgroundImageView;

    @FXML
    private StackPane root;
    @FXML

    private void initialize() {
    HttpsController.getInstance(this);
    // TODO: Read the background image from the config file
    backgroundImageView.setImage(new Image(getClass().getResource("/images/background.jpg").toExternalForm()));

    root.widthProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
    root.heightProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
    root.setPadding(new Insets(70));

    VBox overlayBox = new VBox(10);
    overlayBox.setPadding(new Insets(50));
    overlayBox.setBackground(new Background(new BackgroundFill(
        Color.color(1, 0, 1, 0.6), CornerRadii.EMPTY, Insets.EMPTY
    )));
    overlayBox.setMaxWidth(Double.MAX_VALUE);
    overlayBox.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(overlayBox, Priority.ALWAYS);

    Team[] teams = DatenManager.getInstance().getTeams();
    if (teams.length == 0) return;

    int maxPoints = 0;
    for (Team team : teams) {
        int punkte = DatenManager.getInstance().getPunkte(team);
        if (punkte > maxPoints) {
            maxPoints = punkte;
        }
    }

for (Team team : teams) {
    String teamName = team.getName();
    int punkte = DatenManager.getInstance().getPunkte(team);

    Label nameLabel = new Label(teamName);
    nameLabel.setPrefWidth(100);
    nameLabel.setTextFill(Color.WHITE);

    Label punkteLabel = new Label(String.valueOf(punkte));
    punkteLabel.setPrefWidth(50);
    punkteLabel.setTextFill(Color.WHITE);
    punkteLabel.setAlignment(Pos.CENTER_RIGHT);
    punkteLabel.setTextAlignment(javafx.scene.text.TextAlignment.RIGHT);

    // Balken-Container als StackPane für vertikale Zentrierung
    StackPane barContainer = new StackPane();
    HBox.setHgrow(barContainer, Priority.ALWAYS);

    Rectangle bar = new Rectangle();
    bar.setArcWidth(5);
    bar.setArcHeight(5);
    bar.setFill(team.getFarbe());

    // Balkenbreite anpassen (wie gehabt)
    double widthRatio = (maxPoints == 0) ? 0 : (double) punkte / maxPoints;
    bar.widthProperty().bind(barContainer.widthProperty().multiply(widthRatio));

    // Höhe des Balkens soll mit HBox mitwachsen, mit etwas Abstand
    bar.heightProperty().bind(barContainer.heightProperty().subtract(10));

    barContainer.getChildren().add(bar);
    barContainer.setAlignment(Pos.CENTER_LEFT);

    HBox entry = new HBox(10, nameLabel, barContainer, punkteLabel);
    entry.setAlignment(Pos.CENTER_LEFT);
    entry.setMaxWidth(Double.MAX_VALUE);
    entry.setPrefHeight(0); // gleichmäßige Verteilung
    VBox.setVgrow(entry, Priority.ALWAYS);

    overlayBox.getChildren().add(entry);
}



    root.getChildren().add(overlayBox);
}


    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Zurück")) {
            try {
                App.setRoot("primary");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}