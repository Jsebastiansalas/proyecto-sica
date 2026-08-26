package com.acme.sica.infraestructura.ui.javafx;

import com.acme.sica.infraestructura.configuracion.ContenedorDependencias;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AplicacionJavaFx extends Application {

    private static ContenedorDependencias contenedorDependencias;

    public static void setContenedorDependencias(ContenedorDependencias contenedor) {
        AplicacionJavaFx.contenedorDependencias = contenedor;
    }

    public static ContenedorDependencias getContenedorDependencias() {
        return contenedorDependencias;
    }

    @Override
    public void start(Stage primaryStage) {
        Label etiqueta = new Label("SICA - Zona Acme\nProyecto cargado correctamente");
        etiqueta.setStyle("-fx-font-size: 16px; -fx-alignment: center;");

        StackPane raiz = new StackPane(etiqueta);
        Scene escena = new Scene(raiz, 400, 300);

        primaryStage.setTitle("SICA - Zona Acme");
        primaryStage.setScene(escena);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}