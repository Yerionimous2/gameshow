package gameshowgui.gui;

import gameshowgui.httpsController.HttpsController;
import gameshowgui.model.DatenManager;
import gameshowgui.model.Team;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
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
    backgroundImageView.setImage(new Image(getClass().getResource("/images/background.jpg").toExternalForm()));

    root.widthProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
    root.heightProperty().addListener((obs, oldVal, newVal) -> adjustBackgroundSize());
    root.setPadding(new Insets(70));

    VBox overlayBox = new VBox(10);
    overlayBox.setPadding(new Insets(20));
    overlayBox.setBackground(new Background(new BackgroundFill(
        Color.color(1, 0, 1, 0.6), CornerRadii.EMPTY, Insets.EMPTY
    )));

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

        // Label links (Name)
        Label nameLabel = new Label(teamName);
        nameLabel.setPrefWidth(100);

        // Balken
        double widthRatio = punkte / (double) maxPoints;
        Rectangle bar = new Rectangle(200 * widthRatio, 20, Color.ORANGE);
        bar.setArcWidth(5);
        bar.setArcHeight(5);

        // Label rechts (Punkte)
        Label punkteLabel = new Label(String.valueOf(punkte));
        punkteLabel.setPrefWidth(50);

        HBox entry = new HBox(10, nameLabel, bar, punkteLabel);
        overlayBox.getChildren().add(entry);
    }

    root.getChildren().add(overlayBox);
}


    public void handleMessage(String empfangeneNachricht) {
        // TODO: Hier muss nur "Zurück" verarbeitet werden.
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
