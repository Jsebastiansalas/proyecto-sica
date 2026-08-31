package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioJdbcPersona implements PersonaRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcPersona(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    @Override
    public Persona guardar(Persona persona) {
        if (persona.getId() == null) {
            return insertar(persona);
        }
        return actualizar(persona);
    }

    private Persona insertar(Persona persona) {
        String sql = "INSERT INTO personas (documento, nombre, foto_url, tipo, bloqueada, motivo_bloqueo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, persona.getDocumentoIdentidad());
            stmt.setString(2, persona.getNombreCompleto());
            stmt.setString(3, persona.getFotoUrl());
            stmt.setString(4, persona.getTipo().name());
            stmt.setBoolean(5, persona.isBloqueada());
            stmt.setString(6, persona.getMotivoBloqueo());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    persona.setId(claves.getLong(1));
                }
            }
            return persona;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar persona", e);
        }
    }

    private Persona actualizar(Persona persona) {
        String sql = "UPDATE personas SET documento = ?, nombre = ?, foto_url = ?, tipo = ?, bloqueada = ?, motivo_bloqueo = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, persona.getDocumentoIdentidad());
            stmt.setString(2, persona.getNombreCompleto());
            stmt.setString(3, persona.getFotoUrl());
            stmt.setString(4, persona.getTipo().name());
            stmt.setBoolean(5, persona.isBloqueada());
            stmt.setString(6, persona.getMotivoBloqueo());
            stmt.setLong(7, persona.getId());
            stmt.executeUpdate();
            return persona;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar persona", e);
        }
    }

    @Override
    public Optional<Persona> buscarPorId(Long id) {
        String sql = "SELECT id, documento, nombre, foto_url, tipo, bloqueada, motivo_bloqueo, fecha_creacion FROM personas WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona por id", e);
        }
    }

    @Override
    public Optional<Persona> buscarPorDocumento(String documento) {
        String sql = "SELECT id, documento, nombre, foto_url, tipo, bloqueada, motivo_bloqueo, fecha_creacion FROM personas WHERE documento = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona por documento", e);
        }
    }

    @Override
    public List<Persona> listarTodos() {
        String sql = "SELECT id, documento, nombre, foto_url, tipo, bloqueada, motivo_bloqueo, fecha_creacion FROM personas ORDER BY nombre";
        List<Persona> personas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                personas.add(mapearFila(rs));
            }
            return personas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar personas", e);
        }
    }

    @Override
    public List<Persona> listarBloqueadas() {
        String sql = "SELECT id, documento, nombre, foto_url, tipo, bloqueada, motivo_bloqueo, fecha_creacion FROM personas WHERE bloqueada = TRUE ORDER BY nombre";
        List<Persona> personas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                personas.add(mapearFila(rs));
            }
            return personas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar personas bloqueadas", e);
        }
    }

    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM personas WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar persona", e);
        }
    }

    @Override
    public boolean existePorDocumento(String documento) {
        String sql = "SELECT 1 FROM personas WHERE documento = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar persona por documento", e);
        }
    }

    @Override
    public long contar() {
        String sql = "SELECT COUNT(*) FROM personas";
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar personas", e);
        }
    }

    private Persona mapearFila(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setId(rs.getLong("id"));
        persona.setDocumentoIdentidad(rs.getString("documento"));
        persona.setNombreCompleto(rs.getString("nombre"));
        persona.setFotoUrl(rs.getString("foto_url"));
        persona.setTipo(TipoPersona.valueOf(rs.getString("tipo")));
        persona.setBloqueada(rs.getBoolean("bloqueada"));
        persona.setMotivoBloqueo(rs.getString("motivo_bloqueo"));
        persona.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        return persona;
    }
}
