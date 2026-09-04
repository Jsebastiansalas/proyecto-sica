package com.acme.sica.infraestructura.ui.javafx;

import com.acme.sica.infraestructura.configuracion.ContenedorDependencias;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Punto de arranque de la aplicación de escritorio construida con JavaFX.
 */
public class AplicacionJavaFx extends Application {

    private static ContenedorDependencias contenedorDependencias;

    public static void setContenedorDependencias(ContenedorDependencias contenedor) {
        AplicacionJavaFx.contenedorDependencias = contenedor;
    }

    public static ContenedorDependencias getContenedorDependencias() {
        return contenedorDependencias;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        double ancho = Screen.getPrimary().getVisualBounds().getWidth() * 0.9;
        double alto = Screen.getPrimary().getVisualBounds().getHeight() * 0.9;
        Scene escena = new Scene(raiz, ancho, alto);
        escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setTitle("SICA - Zona Acme");
        primaryStage.setScene(escena);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(true);
        NavegacionHelper.pantallaCompleta(primaryStage);
        primaryStage.show();
    }

}
