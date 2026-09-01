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
     * Aplica pantalla completa a una ventana, sin el aviso de salida.
     * @param stage Ventana a maximizar a pantalla completa
     */
    public static void pantallaCompleta(Stage stage) {
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
    }

    /**
     * Navega al dashboard desde cualquier controlador.
     * Reemplaza solo el root de la escena existente: la ventana nunca se
     * reconfigura, por lo que la pantalla completa no sufre transiciones.
     * @param origen Node de origen (botón o label) para obtener la Scene
     */
    public static void volverAlDashboard(javafx.scene.Node origen) {
        try {
            Parent raiz = FXMLLoader.load(NavegacionHelper.class.getResource(FXML_DASHBOARD));
            Scene escena = origen.getScene();
            String css = NavegacionHelper.class.getResource(CSS).toExternalForm();
            if (!escena.getStylesheets().contains(css)) {
                escena.getStylesheets().add(css);
            }
            Stage stage = (Stage) escena.getWindow();
            stage.setTitle(TITULO_DASHBOARD);
            escena.setRoot(raiz);
        } catch (IOException e) {
            throw new RuntimeException("Error al volver al dashboard", e);
        }
    }
}
