module gameshowgui {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens gameshowgui to javafx.fxml;
    exports gameshowgui;
}
