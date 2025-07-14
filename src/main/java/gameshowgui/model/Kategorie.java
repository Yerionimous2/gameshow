package gameshowgui.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class Kategorie implements Serializable {
	String name;
	Frage[] fragen;
	
	public Kategorie(String name, Frage[] fragen) {
		this.name = name;
		this.fragen = fragen;
	}
	
	public void sort() {
		Arrays.sort(fragen, Comparator.comparingInt(Frage::getPunkte));
	}
	
	public String getName() {
		return this.name;
	}
	
	public Frage[] getFragen() {
		return this.fragen;
	}

	public void setFragen(Frage[] fragen) {
		this.fragen = fragen;
	}
	
	public String toString() {
		String result = this.name + "(";
		for(Frage frage: this.fragen) {
			result += Integer.toString(frage.getPunkte()) + ",";
		}
		result += ")";
		return result;
	}

    public void setName(String trim) {
		this.name = trim;
    }
}