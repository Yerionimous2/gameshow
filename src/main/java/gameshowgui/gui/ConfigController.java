package gameshowgui.gui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import gameshowgui.httpsController.HttpsController;
import gameshowgui.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ConfigController {

    @FXML
    private TreeView<Object> treeView;
    @FXML 
    private VBox editorBox;
    @FXML 
    private TextField kategorieNameField;
    @FXML 
    private TextField frageTextField;
    @FXML 
    private Spinner<Integer> punkteSpinner;
    @FXML 
    private TextField teamNameField;
    @FXML 
    private ColorPicker teamColorPicker;
    @FXML 
    private Button btnAddKategorie;
    @FXML 
    private Button btnAddFrage;
    @FXML 
    private Button btnAddTeam;
    @FXML 
    private VBox kategorieBox;
    @FXML 
    private VBox frageBox;
    @FXML 
    private VBox teamBox;
    @FXML
    private Label mediumLabel;

    private Kategorie[] kategorien;
    private Object selectedItem;

    @FXML
    public void initialize() {
        HttpsController.getInstance(this);
        kategorien = DatenManager.getInstance().getKategorien();
        punkteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 100));
        reloadTree();
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showDetails(newVal));

        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item instanceof Kategorie) {
                    Kategorie kategorie = (Kategorie) item;
                    setText(kategorie.getName());
                } else if (item instanceof FotoFrage) {
                    Frage frage = (Frage) item;
                    setText(frage.getPunkte() + "");
                    setTextFill(Color.BLUE);
                } else if (item instanceof VideoFrage) {
                    Frage frage = (Frage) item;
                    setText(frage.getPunkte() + "");
                    setTextFill(Color.RED);
                } else if (item instanceof MusikFrage) {
                    Frage frage = (Frage) item;
                    setText(frage.getPunkte() + "");
                    setTextFill(Color.GREEN);
                } else if (item instanceof Frage) {
                    Frage frage = (Frage) item;
                    setText(frage.getPunkte() + "");
                } else if (item instanceof Team) {
                    Team team = (Team) item;
                    setText(team.getName());
                } else {
                    setText(item.toString()); // Fallback
                }
            }
        });
        setButtonsVisible(false, false, false);
    }

    private void reloadTree() {
        kategorien = DatenManager.getInstance().getKategorien();
        TreeItem<Object> currentRoot = treeView.getRoot();
        TreeItem<Object> root = new TreeItem<>("Wurzel");
        root.setExpanded(true);
        Set<String> expandedPaths = currentRoot != null
            ? getExpandedPaths(currentRoot, "Wurzel")
            : new HashSet<>();

        TreeItem<Object> kategorienRoot = new TreeItem<>("Kategorien");
        kategorienRoot.setExpanded(true);
        for (Kategorie k : kategorien) {
            TreeItem<Object> kategorieItem = new TreeItem<>(k);
            for (Frage f : k.getFragen()) {
                kategorieItem.getChildren().add(new TreeItem<>(f));
            }
            kategorienRoot.getChildren().add(kategorieItem);
        }
        root.getChildren().add(kategorienRoot);

        // Team-Knoten
        TreeItem<Object> teamsRoot = new TreeItem<>("Teams");
        teamsRoot.setExpanded(true);
        for (Team t : DatenManager.getInstance().getTeams()) {
            teamsRoot.getChildren().add(new TreeItem<>(t));
        }
        root.getChildren().add(teamsRoot);

        treeView.setRoot(root);
        treeView.setShowRoot(false);

        restoreExpandedPaths(root, "Wurzel", expandedPaths);
    }


    private Set<String> getExpandedPaths(TreeItem<Object> item, String path) {
        Set<String> expandedPaths = new HashSet<>();
        if (item.isExpanded()) {
            expandedPaths.add(path);
        }
        for (TreeItem<Object> child : item.getChildren()) {
            String childPath = path + "/" + getItemIdentifier(child.getValue());
            expandedPaths.addAll(getExpandedPaths(child, childPath));
        }
        return expandedPaths;
    }

    private void restoreExpandedPaths(TreeItem<Object> item, String path, Set<String> expandedPaths) {
        if (expandedPaths.contains(path)) {
            item.setExpanded(true);
        }
        for (TreeItem<Object> child : item.getChildren()) {
            String childPath = path + "/" + getItemIdentifier(child.getValue());
            restoreExpandedPaths(child, childPath, expandedPaths);
        }
    }

    private String getItemIdentifier(Object obj) {
        if (obj instanceof Kategorie) {
            return ((Kategorie) obj).getName();
        } else if (obj instanceof Frage) {
            return Integer.toString(((Frage) obj).getPunkte()); // oder ID
        } else {
            return obj.toString();
        }
    }

    private void showDetails(TreeItem<Object> item) {
        if (item == null) {
            selectedItem = null;
            setButtonsVisible(false, false, false);
            clearEditorFields();
            return;
        }

        Object value = item.getValue();
        selectedItem = value;

        if (value instanceof Kategorie) {
            Kategorie kategorie = (Kategorie) value;
            kategorieNameField.setText(kategorie.getName());
            frageTextField.clear();
            punkteSpinner.getValueFactory().setValue(100);
            teamNameField.clear();
            teamColorPicker.setValue(Color.WHITE);
            setItemsVisible(kategorie);
            setButtonsVisible(true, true, false);
        } else if (value instanceof Frage) {
            Frage frage = (Frage) value;
            frageTextField.setText(frage.getText());
            punkteSpinner.getValueFactory().setValue(frage.getPunkte());
            kategorieNameField.clear();
            teamNameField.clear();
            teamColorPicker.setValue(Color.WHITE);
            setItemsVisible(frage);
            setButtonsVisible(false, false, false);
        } else if (value instanceof Team) {
            Team team = (Team) value;
            teamNameField.setText(team.getName());
            teamColorPicker.setValue(team.getFarbe());
            kategorieNameField.clear();
            frageTextField.clear();
            setItemsVisible(team);
            punkteSpinner.getValueFactory().setValue(100);

            setButtonsVisible(false, false, true);
        } else if ("Kategorien".equals(value.toString())) {
            // "Kategorien" Knoten ausgewählt
            clearEditorFields();
            setButtonsVisible(true, false, false);
        } else if ("Teams".equals(value.toString())) {
            clearEditorFields();
            setButtonsVisible(false, false, true);
        } else {
            clearEditorFields();
            setButtonsVisible(false, false, false);
        }
        if(value instanceof FotoFrage) {
            mediumLabel.setText("Foto: " + ((FotoFrage) value).getLink());
        } else if(value instanceof VideoFrage) {
            mediumLabel.setText("Video: " + ((VideoFrage) value).getLink());
        } else if(value instanceof MusikFrage) {
            mediumLabel.setText("Musik: " + ((MusikFrage) value).getLink());
        } else {
            mediumLabel.setText("Kein Medium ausgewählt");
        }
    }

    private void setItemsVisible(Object item) {
    if (item instanceof Team) {
        teamBox.setVisible(true);
        teamBox.setManaged(true);
        frageBox.setVisible(false);
        frageBox.setManaged(false);
        kategorieBox.setVisible(false);
        kategorieBox.setManaged(false);
    } else if (item instanceof Frage) {
        frageBox.setVisible(true);
        frageBox.setManaged(true);
        teamBox.setVisible(false);
        teamBox.setManaged(false);
        kategorieBox.setVisible(false);
        kategorieBox.setManaged(false);
    } else if (item instanceof Kategorie) {
        kategorieBox.setVisible(true);
        kategorieBox.setManaged(true);
        frageBox.setVisible(false);
        frageBox.setManaged(false);
        teamBox.setVisible(false);
        teamBox.setManaged(false);
    } else {
        kategorieBox.setVisible(false);
        kategorieBox.setManaged(false);
        frageBox.setVisible(false);
        frageBox.setManaged(false);
        teamBox.setVisible(false);
        teamBox.setManaged(false);
    }
}


    private void setButtonsVisible(boolean kategorie, boolean frage, boolean team) {
        btnAddKategorie.setVisible(kategorie);
        btnAddKategorie.setManaged(kategorie);

        btnAddFrage.setVisible(frage);
        btnAddFrage.setManaged(frage);

        btnAddTeam.setVisible(team);
        btnAddTeam.setManaged(team);
    }

    private void clearEditorFields() {
        kategorieNameField.clear();
        frageTextField.clear();
        punkteSpinner.getValueFactory().setValue(100);
        teamNameField.clear();
        teamColorPicker.setValue(Color.WHITE);
    }


    @FXML
    private void handleSpeichern() {
        if (selectedItem instanceof Kategorie) {
            Kategorie kategorie = (Kategorie) selectedItem;
            kategorie.setName(kategorieNameField.getText().trim());
        } else if (selectedItem instanceof Frage) {
            Frage frage = (Frage) selectedItem;
            frage.setText(frageTextField.getText().trim());
            frage.setPunkte(punkteSpinner.getValue());
            Kategorie kategorie = DatenManager.getInstance().findeKategorie(frage);
            if(kategorie != null) {
                kategorie.sort();
            }
        } else if (selectedItem instanceof Team) {
        Team team = (Team) selectedItem;
        team.setName(teamNameField.getText().trim());
        // Farbe setzen, z.B. von einem ColorPicker
        Color neueFarbe = teamColorPicker.getValue();
        if (neueFarbe != null) {
            team.setTeamFarbe(neueFarbe);
        }
    }

        DatenManager.getInstance().speichern();
        reloadTree();
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Bestätigung");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    @FXML
    private void onDeleteKategorie() {
        if (selectedItem instanceof Kategorie && confirm("Wirklich Kategorie "+ ((Kategorie) selectedItem).getName() + " löschen?")) {
            Kategorie kategorie = (Kategorie) selectedItem;
            DatenManager.getInstance().removeKategorie(kategorie);
            DatenManager.getInstance().speichern();
            reloadTree();
        }
    }

    @FXML
    private void onDeleteFrage() {
        if (selectedItem instanceof Frage) {
            Frage frage = (Frage) selectedItem;
            Kategorie kategorie = DatenManager.getInstance().findeKategorie(frage);
            if (kategorie != null) {
                DatenManager.getInstance().removeFrage(kategorie, frage);
                DatenManager.getInstance().speichern();
                reloadTree();
            }
        }
    }

    @FXML
    private void onDeleteTeam() {
        if (selectedItem instanceof Team) {
            Team team = (Team) selectedItem;
            DatenManager.getInstance().removeTeam(team);
            DatenManager.getInstance().speichern();
            reloadTree();
        }
    }

    @FXML
    private void onAddTeam() {
        Team neuesTeam = new Team(Color.WHITE, "Neues Team");
        DatenManager.getInstance().addTeam(neuesTeam);
        DatenManager.getInstance().speichern();
        reloadTree();
    }

    @FXML
    private void onAddKategorie() {
        Kategorie neueKategorie = new Kategorie("Neue Kategorie", new Frage[0]);
        DatenManager.getInstance().addKategorie(neueKategorie);
        DatenManager.getInstance().speichern();
        reloadTree();
    }

    @FXML
    private void onAddFrage() {
        TreeItem<Object> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.getValue() instanceof Kategorie) {
            Kategorie kategorie = (Kategorie) selectedItem.getValue();
            Frage neueFrage = new Frage(0, "Neue Frage");
            DatenManager.getInstance().addFrage(kategorie, neueFrage);

            TreeItem<Object> frageItem = new TreeItem<>(neueFrage);
            selectedItem.getChildren().add(frageItem);
            selectedItem.setExpanded(true);

            DatenManager.getInstance().speichern();
        }
    }

    @FXML
    private void onRemoveMedium() {
    if (selectedItem instanceof VideoFrage || selectedItem instanceof FotoFrage || selectedItem instanceof MusikFrage) {
        Frage frage = (Frage) selectedItem;
        Frage neueFrage = new Frage(frage.getPunkte(), frage.getText());
        DatenManager.getInstance().ersetzeFrage(frage, neueFrage);
        DatenManager.getInstance().speichern();
        reloadTree();
    }
}

    @FXML
    private void onAddVideo() {
        if (selectedItem instanceof Frage && !(selectedItem instanceof VideoFrage)) {
            Frage frage = (Frage) selectedItem;
            String pfad = chooseMediaFile("Video", "*.mp4");
            if (pfad != null) {
                Frage neueFrage = new VideoFrage(frage.getPunkte(), frage.getText(), pfad);
                DatenManager.getInstance().ersetzeFrage(frage, neueFrage);
                DatenManager.getInstance().speichern();
                reloadTree();
            }
        }
    }

    @FXML
    private void onAddFoto() {
        if (selectedItem instanceof Frage && !(selectedItem instanceof FotoFrage)) {
            Frage frage = (Frage) selectedItem;
            String pfad = chooseMediaFile("Bild", "*.jpg", "*.jpeg", "*.png");
            if (pfad != null) {
                Frage neueFrage = new FotoFrage(frage.getPunkte(), frage.getText(), pfad);
                DatenManager.getInstance().ersetzeFrage(frage, neueFrage);
                DatenManager.getInstance().speichern();
                reloadTree();
            }
        }
    }

    @FXML
    private void onAddMusik() {
        if (selectedItem instanceof Frage && !(selectedItem instanceof MusikFrage)) {
            Frage frage = (Frage) selectedItem;
            String pfad = chooseMediaFile("Musik", "*.mp3", "*.wav");
            if (pfad != null) {
                Frage neueFrage = new MusikFrage(frage.getPunkte(), frage.getText(), pfad);
                DatenManager.getInstance().ersetzeFrage(frage, neueFrage);
                DatenManager.getInstance().speichern();
                reloadTree();
            }
        }
    }

    private String chooseMediaFile(String title, String... extensions) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Wähle " + title + " aus");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter(title + "-Dateien", extensions));
        java.io.File file = fileChooser.showOpenDialog(null);
        String result = "";
        if (file != null) {
            result =file.toURI().toString();
        }
        return result;
    }


    @FXML
    private void onFarbenBearbeiten() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FarbenEditor.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Farbeneditor");
            stage.setScene(scene);
            stage.setWidth(1400);  // Breite in Pixel
            stage.setHeight(1000); // Höhe in Pixel
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(String empfangeneNachricht) {
        String[] teile = empfangeneNachricht.split(",");
        if(teile[0].equals("Zurück")) {
            wechselZuUebersicht();
        }
    }

    public void wechselZuUebersicht() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
