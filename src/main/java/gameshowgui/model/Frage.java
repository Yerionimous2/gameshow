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
		Color bgColor = DatenManager.getInstance().getDesignEinstellungen().getHintergrundfarbe();
        return new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 0.6);
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

    public void setText(String trim) {
		this.text = trim;
    }

    public void setPunkte(Integer value) {
		this.punkte = value;
    }
}
