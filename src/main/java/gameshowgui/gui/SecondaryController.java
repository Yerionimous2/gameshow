package gameshowgui.gui;

import java.io.IOException;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void wechselZuÜbersicht() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Team")) {
            // TODO: Team teile[1] die Frage zuweisen
            wechselZuÜbersicht();
        } else if(teile[0].equals("Zurück")) {
            wechselZuÜbersicht();
        }
    }
}