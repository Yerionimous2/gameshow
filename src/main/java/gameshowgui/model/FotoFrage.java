package gameshowgui.model;

public class FotoFrage extends Frage{
	String fotoLink;
	public FotoFrage(int punkte, String text, String fotoLink) {
		super(punkte, text);
		this.fotoLink = fotoLink;
	}

	public String getLink() {
		return fotoLink;
	}
}

