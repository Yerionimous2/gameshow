package gameshowgui.gui;
import gameshowgui.model.Frage;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class FragenPanel extends Button{

	public FragenPanel(Frage frage) {
		super(Integer.toString(frage.getPunkte()));
		Color farbe = frage.getFarbe();
		int red = (int) (farbe.getRed() * 255);
        int green = (int) (farbe.getGreen() * 255);
        int blue = (int) (farbe.getBlue() * 255);

        double alpha = farbe.getOpacity();

        // Im CSS-Stil:
        String style = String.format(
            "-fx-background-color: rgba(%d, %d, %d, %.2f);",
            red, green, blue, alpha
        );

        this.setStyle(style);
	}
	
	public void aktualisiere(Frage frage) {
		Color farbe = frage.getFarbe();
		int red = (int) (farbe.getRed() * 255);
        int green = (int) (farbe.getGreen() * 255);
        int blue = (int) (farbe.getBlue() * 255);

        double alpha = farbe.getOpacity();

        // Im CSS-Stil:
        String style = String.format(
            "-fx-background-color: rgba(%d, %d, %d, %.2f);",
            red, green, blue, alpha
        );

        this.setStyle(style);
	}
}