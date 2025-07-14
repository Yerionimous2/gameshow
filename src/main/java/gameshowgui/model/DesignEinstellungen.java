package gameshowgui.model;

import javafx.scene.paint.Color;
import java.io.Serializable;

public class DesignEinstellungen implements Serializable {
    private static final long serialVersionUID = 1L;

    private String hintergrundbild;
    private String hintergrundfarbeHex = "#FFFFFF";
    private String textfarbeHex = "#000000";

    public String getHintergrundbild() {
        return hintergrundbild;
    }

    public void setHintergrundbild(String uri) {
        this.hintergrundbild = uri;
    }

    public Color getHintergrundfarbe() {
        return Color.web(hintergrundfarbeHex);
    }

    public void setHintergrundfarbe(Color color) {
        this.hintergrundfarbeHex = color.toString();
    }

    public Color getTextfarbe() {
        return Color.web(textfarbeHex);
    }

    public void setTextfarbe(Color color) {
        this.textfarbeHex = color.toString();
    }
}