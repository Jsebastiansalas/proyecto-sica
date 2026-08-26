package com.acme.sica.infrastructure.ui.javafx;

import com.acme.sica.infrastructure.config.DependencyContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JavaFxApplication extends Application {

    private static DependencyContainer dependencyContainer;

    public static void setDependencyContainer(DependencyContainer container) {
        JavaFxApplication.dependencyContainer = container;
    }

    public static DependencyContainer getDependencyContainer() {
        return dependencyContainer;
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("SICA - Zona Acme\nProyecto cargado correctamente");
        label.setStyle("-fx-font-size: 16px; -fx-alignment: center;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("SICA - Zona Acme");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}