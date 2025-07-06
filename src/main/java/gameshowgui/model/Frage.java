package gameshowgui.model;

import java.io.Serializable;

import javafx.scene.paint.Color;


public class Frage implements Serializable {
	int punkte;
	Team team;
	String text;

	public Frage(int punkte, String text) {
		team = null;
		this.punkte = punkte;
		this.text = text;
	}

	public int getPunkte() {
		return punkte;
	}

	public Color getFarbe() {
		if(this.team != null) {
			return this.team.getFarbe();
		}
		// TODO: Standard Fragenfarbe aus Config auslesen
		return new Color(1, 0, 1, 0.6f);
	}
	
	public String getTitel(String kategorie) {
		String result = kategorie;
		result += " " + punkte;
		return result;
	}
	
	public String getText() {
		return this.text;
	}

	public Team getTeam() {
		return this.team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
}
