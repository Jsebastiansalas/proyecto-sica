package com.acme.sica.infraestructura.persistencia.jdbc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Ejecuta los scripts schema.sql y data.sql contra la base de datos.
 * Útil para inicializar la BD la primera vez que corre la aplicación.
 */
public class InicializadorBaseDatos {

    private final FabricaConexiones fabricaConexiones;

    public InicializadorBaseDatos(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    public void inicializar() {
        ejecutarScript("schema.sql");
        ejecutarScript("data.sql");
    }

    private void ejecutarScript(String nombreRecurso) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(nombreRecurso)) {
            if (input == null) {
                throw new RuntimeException("No se encontró el script: " + nombreRecurso);
            }
            String sql = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .replaceAll("(?m)--[^\\r\\n]*", "")
                    .replaceAll("/\\*.*?\\*/", "");

            try (Connection conn = fabricaConexiones.crearConexion();
                 Statement stmt = conn.createStatement()) {
                Arrays.stream(sql.split(";"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .forEach(sentencia -> ejecutarSilenciosamente(stmt, sentencia));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al ejecutar script " + nombreRecurso, e);
        }
    }

    private void ejecutarSilenciosamente(Statement stmt, String sql) {
        try {
            stmt.execute(sql);
        } catch (Exception e) {
            String msg = e.getMessage().toLowerCase();
            // Ignorar errores de reinicio: tablas/índices que ya existen o datos duplicados
            if (!msg.contains("already exists") &&
                !msg.contains("ya existe") &&
                !msg.contains("duplicate entry") &&
                !msg.contains("entrada duplicada") &&
                !msg.contains("duplicate key name") &&
                !msg.contains("duplicate column name")) {
                System.err.println("Advertencia al ejecutar SQL: " + e.getMessage());
            }
        }
    }

}
