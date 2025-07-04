package gameshowgui.gui;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }

    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Team")) {
            // TODO: Team teile[1] die Frage zuweisen
        } else if(teile[0].equals("Zurück")) {
            // TODO: Wechsel zu PrimaryController
        }
    }
}