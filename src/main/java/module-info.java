module gameshowgui {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens gameshowgui.gui to javafx.fxml;
    exports gameshowgui.gui;
    exports gameshowgui.model;
}
