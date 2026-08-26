package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.infraestructura.configuracion.ConfiguracionBaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FabricaConexiones {

    private final ConfiguracionBaseDatos configuracion;

    public FabricaConexiones(ConfiguracionBaseDatos configuracion) {
        this.configuracion = configuracion;
        registrarDriver();
    }

    private void registrarDriver() {
        try {
            Class.forName(configuracion.getDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver JDBC de MySQL", e);
        }
    }

    public Connection crearConexion() throws SQLException {
        return DriverManager.getConnection(
                configuracion.getUrl(),
                configuracion.getUsername(),
                configuracion.getPassword()
        );
    }

}