module gameshowgui {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires org.eclipse.jetty.server;
    requires org.eclipse.jetty.util;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;
    requires javafx.base;

    opens gameshowgui.gui to javafx.fxml;
    exports gameshowgui.gui;
    exports gameshowgui.model;

}
