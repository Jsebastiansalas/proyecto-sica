package com.acme.sica.infraestructura.configuracion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfiguracionBaseDatos {

    private final Properties propiedades;

    public ConfiguracionBaseDatos() {
        this.propiedades = new Properties();
        cargarPropiedades();
    }

    private void cargarPropiedades() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                propiedades.load(input);
            } else {
                cargarPredeterminados();
            }
        } catch (IOException e) {
            cargarPredeterminados();
        }
    }

    private void cargarPredeterminados() {
        propiedades.setProperty("db.url", "jdbc:mysql://localhost:3306/sica_db");
        propiedades.setProperty("db.username", "root");
        propiedades.setProperty("db.password", "");
        propiedades.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
    }

    public String getUrl() {
        return propiedades.getProperty("db.url");
    }

    public String getUsername() {
        return propiedades.getProperty("db.username");
    }

    public String getPassword() {
        return propiedades.getProperty("db.password");
    }

    public String getDriver() {
        return propiedades.getProperty("db.driver");
    }

    public Properties getPropiedades() {
        return propiedades;
    }
}