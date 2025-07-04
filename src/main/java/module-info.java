module gameshowgui {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens gameshowgui.gui to javafx.fxml;
    exports gameshowgui.gui;
}
