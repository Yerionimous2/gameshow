package gameshowgui.model;

public class VideoFrage extends Frage{
	String videoLink;
	public VideoFrage(int punkte, String text, String videoLink) {
		super(punkte, text);
		this.videoLink = videoLink;
	}
}