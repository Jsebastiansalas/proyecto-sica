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
                resolverVariablesEntorno();
            } else {
                cargarPredeterminados();
            }
        } catch (IOException e) {
            cargarPredeterminados();
        }
    }

    /**
     * Reemplaza los placeholders ${VARIABLE:valorPorDefecto} por el valor
     * de la variable de entorno correspondiente (o el default si no existe).
     */
    private void resolverVariablesEntorno() {
        for (String clave : propiedades.stringPropertyNames()) {
            propiedades.setProperty(clave, resolverValor(propiedades.getProperty(clave)));
        }
    }

    private String resolverValor(String valor) {
        if (valor == null || !valor.startsWith("${") || !valor.endsWith("}")) {
            return valor;
        }
        String contenido = valor.substring(2, valor.length() - 1);
        int separador = contenido.indexOf(':');
        String nombre = separador >= 0 ? contenido.substring(0, separador) : contenido;
        String porDefecto = separador >= 0 ? contenido.substring(separador + 1) : "";
        String entorno = System.getenv(nombre);
        return entorno != null ? entorno : porDefecto;
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