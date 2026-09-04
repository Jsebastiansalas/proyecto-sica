package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.infraestructura.configuracion.ConfiguracionBaseDatos;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fábrica encargada de crear y proveer conexiones JDBC a la base de datos.
 */
public class FabricaConexiones {

    private static final int TAMANO_MINIMO_IDLE = 2;
    private static final int TAMANO_MAXIMO_POOL = 10;
    private static final int TIMEOUT_CONEXION_MS = 30000;
    private static final int TIMEOUT_IDLE_MS = 600000;
    private static final int VIDA_MAXIMA_MS = 1800000;

    private final HikariDataSource dataSource;

    public FabricaConexiones(ConfiguracionBaseDatos configuracion) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(configuracion.getUrl());
        config.setUsername(configuracion.getUsername());
        config.setPassword(configuracion.getPassword());
        config.setDriverClassName(configuracion.getDriver());
        config.setMinimumIdle(TAMANO_MINIMO_IDLE);
        config.setMaximumPoolSize(TAMANO_MAXIMO_POOL);
        config.setConnectionTimeout(TIMEOUT_CONEXION_MS);
        config.setIdleTimeout(TIMEOUT_IDLE_MS);
        config.setMaxLifetime(VIDA_MAXIMA_MS);
        config.setPoolName("SICA-HikariPool");
        this.dataSource = new HikariDataSource(config);
    }

    public Connection crearConexion() throws SQLException {
        return dataSource.getConnection();
    }

    public void cerrar() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}