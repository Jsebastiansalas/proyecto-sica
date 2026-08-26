package com.acme.sica;

import com.acme.sica.infrastructure.config.DependencyContainer;
import com.acme.sica.infrastructure.config.DatabaseConfig;
import com.acme.sica.infrastructure.ui.javafx.JavaFxApplication;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        DependencyContainer container = new DependencyContainer(databaseConfig);
        JavaFxApplication.setDependencyContainer(container);

        Application.launch(JavaFxApplication.class, args);
    }

}