package com.acme.sica.infraestructura.ui.javafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Helper para navegación entre pantallas JavaFX.
 */
public final class NavegacionHelper {

    private static final String FXML_DASHBOARD = "/fxml/dashboard.fxml";
    private static final String CSS = "/css/application.css";
    private static final String TITULO_DASHBOARD = "SICA - Dashboard";

    private NavegacionHelper() {}

    /**
     * Navega al dashboard desde cualquier controlador.
     * @param origen Node de origen (botón o label) para obtener el Stage
     */
    public static void volverAlDashboard(javafx.scene.Node origen) {
        try {
            Parent raiz = FXMLLoader.load(NavegacionHelper.class.getResource(FXML_DASHBOARD));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(NavegacionHelper.class.getResource(CSS).toExternalForm());
            Stage stage = (Stage) origen.getScene().getWindow();
            stage.setScene(escena);
            stage.setTitle(TITULO_DASHBOARD);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error al volver al dashboard", e);
        }
    }
}
