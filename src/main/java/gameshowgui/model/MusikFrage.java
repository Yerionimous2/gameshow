package gameshowgui.model;

public class MusikFrage extends Frage{
	String musikLink;
	public MusikFrage(int punkte, String text, String musikLink) {
		super(punkte, text);
		this.musikLink = musikLink;
	}
}