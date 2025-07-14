package gameshowgui.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatenManager {
    private static DatenManager instance;
    private DatenEinwickler datenEinwickler;
    private DesignEinstellungen design;
    private static final Path DATEIPFAD = Paths.get(System.getProperty("user.home"), ".gameshow", "daten.ser");
    private static final Path DATEIPFADFARBEN = Paths.get(System.getProperty("user.home"), ".gameshow", "farben.ser");

    private DatenManager() {
        try {
            Files.createDirectories(DATEIPFAD.getParent());

            if (Files.exists(DATEIPFAD)) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATEIPFAD.toFile()))) {
                    datenEinwickler = (DatenEinwickler) ois.readObject();
                }
            } else {
                datenEinwickler = new DatenEinwickler(new Kategorie[0], new Team[0]);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            datenEinwickler = new DatenEinwickler(new Kategorie[0], new Team[0]);
        }
        try {
            Files.createDirectories(DATEIPFADFARBEN.getParent());

            if (Files.exists(DATEIPFADFARBEN)) {
                try (ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(DATEIPFADFARBEN.toFile()))) {
                    design = (DesignEinstellungen) ois2.readObject();
                }
            } else {
                design = new DesignEinstellungen();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            design = new DesignEinstellungen();
        }
    }

    public void speichern() {
        try {
            Files.createDirectories(DATEIPFAD.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATEIPFAD.toFile()))) {
                oos.writeObject(datenEinwickler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Files.createDirectories(DATEIPFADFARBEN.getParent());
            try (ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream(DATEIPFADFARBEN.toFile()))) {
                oos2.writeObject(design);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loeschen() {
        try {
            Files.deleteIfExists(DATEIPFAD);
            datenEinwickler = new DatenEinwickler(new Kategorie[0], new Team[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void zurücksetzen() {
        for(Kategorie kategorie : datenEinwickler.getKategorien()) {
            if(kategorie.getName().equals("Anpassungen")) {
                kategorie.setFragen(new Frage[0]);
            }
            for(Frage frage : kategorie.getFragen()) {
                frage.setTeam(null);
            }
        }
        speichern();
    }

    public static DatenManager getInstance() {
        if (instance == null) {
            instance = new DatenManager();
        }
        return instance;
    }

    public Team[] getTeams() {
        return datenEinwickler.getTeams();
    }

    public Kategorie[] getKategorien() {
        return datenEinwickler.getKategorien();
    }

    public void setTeams(Team[] teams) {
        datenEinwickler = new DatenEinwickler(datenEinwickler.getKategorien(), teams);
        speichern();
    }

    public void setKategorien(Kategorie[] kategorien) {
        datenEinwickler = new DatenEinwickler(kategorien, datenEinwickler.getTeams());
        speichern();
    }

    public int getPunkte(Team team) {
        int punkte = 0;
        for (Kategorie kategorie : datenEinwickler.getKategorien()) {
            for (Frage frage : kategorie.getFragen()) {
                if (frage.getTeam() != null && frage.getTeam() == team) {
                    punkte += frage.getPunkte();
                }
            }
        }
        return punkte;
    }

    public Frage findeFrage(String name, int punkte) {
        Frage result = null;
        for (Kategorie kategorie : datenEinwickler.getKategorien()) {
            if (kategorie.getName().equals(name)) {
                for (Frage frage : kategorie.getFragen()) {
                    if (frage.getPunkte() == punkte) {
                        if(result != null) {
                            throw new IllegalStateException("Mehrere Fragen mit dem gleichen Namen und Punkten gefunden: " + name + ", " + punkte);
                        }
                        result = frage;
                    }
                }
            }
        }
        return result;
    }

    public Kategorie findeKategorie(String name) {
        Kategorie result = null;
        for (Kategorie kategorie : datenEinwickler.getKategorien()) {
            if (kategorie.getName().equals(name)) {
                if(result != null) {
                    throw new IllegalStateException("Mehrere Kategorien mit dem gleichen Namen gefunden: " + name);
                }
                result = kategorie;
            }
        }
        return result;
    }

    public Kategorie findeKategorie(Frage frage) {
        for (Kategorie kategorie : datenEinwickler.getKategorien()) {
            for(Frage f : kategorie.getFragen()) {
                if (frage == f) {
                    return kategorie;
                }
            }
        }
        return null;
    }

    public Team findeTeam(String string) {
        Team result = null;
        for(Team team : datenEinwickler.getTeams()) {
            if (team.getName().equals(string)) {
                if(result != null) {
                    throw new IllegalStateException("Mehrere Teams mit dem gleichen Namen gefunden: " + string);
                }
                result = team;
            }
        }
        return result;
    }

    public void addKategorie(Kategorie neueKategorie) {
        Kategorie[] kategorienOrig = datenEinwickler.getKategorien();
        Kategorie[] kategorienNeu = new Kategorie[kategorienOrig.length + 1];
        System.arraycopy(kategorienOrig, 0, kategorienNeu, 0, kategorienOrig.length);
        kategorienNeu[kategorienOrig.length] = neueKategorie;
        datenEinwickler = new DatenEinwickler(kategorienNeu, datenEinwickler.getTeams());
        speichern();
    }

    public void addFrage(Kategorie kategorie, Frage neueFrage) {
        Frage[] fragenOrig = kategorie.getFragen();
        Frage[] fragenNeu = new Frage[fragenOrig.length + 1];
        System.arraycopy(fragenOrig, 0, fragenNeu, 0, fragenOrig.length);
        fragenNeu[fragenOrig.length] = neueFrage;
        kategorie.setFragen(fragenNeu);
        speichern();
    }

    public void addTeam(Team neuesTeam) {
        Team[] teamsOrig = datenEinwickler.getTeams();
        Team[] teamsNeu = new Team[teamsOrig.length + 1];
        System.arraycopy(teamsOrig, 0, teamsNeu, 0, teamsOrig.length);
        teamsNeu[teamsOrig.length] = neuesTeam;
        datenEinwickler = new DatenEinwickler(datenEinwickler.getKategorien(), teamsNeu);
        speichern();
    }

    public void removeFrage(Kategorie kategorie, Frage frage) {
        Frage[] fragenOrig = kategorie.getFragen();
        Frage[] fragenNeu = new Frage[fragenOrig.length - 1];
        int index = 0;
        for (Frage f : fragenOrig) {
            if (f != frage) {
                fragenNeu[index++] = f;
            }
        }
        kategorie.setFragen(fragenNeu);
        speichern();
    }

    public void removeTeam(Team team) {
        Team[] teamsOrig = datenEinwickler.getTeams();
        Team[] teamsNeu = new Team[teamsOrig.length - 1];
        int index = 0;
        for (Team t : teamsOrig) {
            if (t != team) {
                teamsNeu[index++] = t;
            }
        }
        datenEinwickler = new DatenEinwickler(datenEinwickler.getKategorien(), teamsNeu);
        speichern();
    }

    public void removeKategorie(Kategorie kategorie) {
        Kategorie[] kategorienOrig = datenEinwickler.getKategorien();
        Kategorie[] kategorienNeu = new Kategorie[kategorienOrig.length - 1];
        int index = 0;
        for (Kategorie k : kategorienOrig) {
            if (k != kategorie) {
                kategorienNeu[index++] = k;
            }
        }
        datenEinwickler = new DatenEinwickler(kategorienNeu, datenEinwickler.getTeams());
        speichern();
    }

    public DesignEinstellungen getDesignEinstellungen() {
        return design;
    }

    public void ersetzeFrage(Frage frage, Frage neueFrage) {
        for(Kategorie kategorie : datenEinwickler.getKategorien()) {
            Frage[] fragen = kategorie.getFragen();
            for (Frage f: fragen) {
                if(f == frage) {
                    removeFrage(kategorie, frage);
                    addFrage(kategorie, neueFrage);
                    kategorie.sort();
                    speichern();
                    return;
                }
            }
        }
    }
}
