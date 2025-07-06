package gameshowgui.model;

import java.io.Serializable;

import javafx.scene.paint.Color;

public class Team implements Serializable {
	private transient Color teamFarbe;
	private String teamFarbeString;

	public Team(Color teamFarbe) {
		this.teamFarbe = teamFarbe;
		this.teamFarbeString = String.format("#%02X%02X%02X", 
			(int)(teamFarbe.getRed() * 255), 
			(int)(teamFarbe.getGreen() * 255), 
			(int)(teamFarbe.getBlue() * 255));
	}

	public Color getFarbe() {
		if(this.teamFarbe == null && this.teamFarbeString != null) {
			this.teamFarbe = Color.web(this.teamFarbeString);
		}
		return this.teamFarbe;
	}
}
