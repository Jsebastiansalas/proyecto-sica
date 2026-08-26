package com.acme.sica.infrastructure.config;

/**
 * Contenedor manual de dependencias.
 * Se encarga de instanciar y conectar todos los adaptadores, servicios y casos de uso.
 * A medida que avancemos, este archivo irá creciendo con cada funcionalidad nueva.
 */
public class DependencyContainer {

    private final DatabaseConfig databaseConfig;

    public DependencyContainer(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

}