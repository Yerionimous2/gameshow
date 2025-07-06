package gameshowgui.model;

import java.io.Serializable;

public class DatenEinwickler implements Serializable {

    private static final long serialVersionUID = 1L;

    private Kategorie[] kategorien;
    private Team[] teams;

    public DatenEinwickler(Kategorie[] kategorien, Team[] teams) {
        this.kategorien = kategorien;
        this.teams = teams;
    }
    
    public Kategorie[] getKategorien() {
        return kategorien;
    }

    public Team[] getTeams() {
        return teams;
    }
}