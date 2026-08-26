package com.acme.sica.infraestructura.ui.javafx;

import com.acme.sica.infraestructura.configuracion.ContenedorDependencias;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    public void start(Stage primaryStage) throws Exception {
        Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene escena = new Scene(raiz, 400, 500);
        escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setTitle("SICA - Zona Acme");
        primaryStage.setScene(escena);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}