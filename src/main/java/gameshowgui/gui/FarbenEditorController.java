package gameshowgui.gui;

import gameshowgui.model.DatenManager;
import gameshowgui.model.DesignEinstellungen;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;

public class FarbenEditorController {

    @FXML private StackPane previewPane;
    @FXML private Label kategorieLabel;
    @FXML private Label frageLabel;
    @FXML private ColorPicker backgroundColorPicker;
    @FXML private ColorPicker textColorPicker;
    @FXML private VBox textContainer;

    private DesignEinstellungen einstellungen;

    @FXML
    public void initialize() {
        einstellungen = DatenManager.getInstance().getDesignEinstellungen();

        backgroundColorPicker.setValue(einstellungen.getHintergrundfarbe());
        textColorPicker.setValue(einstellungen.getTextfarbe());

        applyStyles();

        backgroundColorPicker.setOnAction(e -> {
            einstellungen.setHintergrundfarbe(backgroundColorPicker.getValue());
            applyStyles();
            DatenManager.getInstance().speichern();
        });

        textColorPicker.setOnAction(e -> {
            einstellungen.setTextfarbe(textColorPicker.getValue());
            applyStyles();
            DatenManager.getInstance().speichern();
        });
    }

    @FXML
    public void onBildAuswaehlen() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Hintergrundbild auswählen");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Bilddateien", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = chooser.showOpenDialog(previewPane.getScene().getWindow());
        if (file != null) {
            einstellungen.setHintergrundbild(file.toURI().toString());
            applyStyles();
            DatenManager.getInstance().speichern();
        }
    }

    private void applyStyles() {
        if (einstellungen.getHintergrundbild() != null) {
            Image img = new Image(einstellungen.getHintergrundbild(), true);
            BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, false, true);
            BackgroundImage bgImage = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
            previewPane.setBackground(new Background(bgImage));
        } else {
            previewPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        }

        Color bgColor = einstellungen.getHintergrundfarbe();
        Color bgColorMitAlpha = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.6);
        BackgroundFill fill = new BackgroundFill(bgColorMitAlpha, null, null);
        frageLabel.setBackground(new Background(fill));

        kategorieLabel.setTextFill(einstellungen.getTextfarbe());
        frageLabel.setTextFill(einstellungen.getTextfarbe());
    }
    

}