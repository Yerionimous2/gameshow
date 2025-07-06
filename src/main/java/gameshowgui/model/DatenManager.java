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
    private static final Path DATEIPFAD = Paths.get(System.getProperty("user.home"), ".gameshow", "daten.ser");

    private DatenManager() {
        try {
            // Verzeichnis erstellen, falls nicht vorhanden
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
    }

    public void speichern() {
        try {
            Files.createDirectories(DATEIPFAD.getParent()); // Sicherstellen, dass das Verzeichnis existiert
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATEIPFAD.toFile()))) {
                oos.writeObject(datenEinwickler);
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
}
