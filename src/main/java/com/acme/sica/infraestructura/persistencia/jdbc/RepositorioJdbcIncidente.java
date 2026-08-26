package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;
import com.acme.sica.dominio.puerto.salida.IncidenteRepositorioPuerto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioJdbcIncidente implements IncidenteRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcIncidente(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    @Override
    public Incidente guardar(Incidente incidente) {
        if (incidente.getId() == null) {
            return insertar(incidente);
        }
        return actualizar(incidente);
    }

    private Incidente insertar(Incidente incidente) {
        String sql = "INSERT INTO incidentes (persona_id, usuario_id, descripcion, gravedad, fecha) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, incidente.getPersona().getId());
            stmt.setLong(2, incidente.getRegistradoPor().getId());
            stmt.setString(3, incidente.getDescripcion());
            stmt.setString(4, incidente.getGravedad().name());
            stmt.setTimestamp(5, Timestamp.valueOf(incidente.getFechaCreacion()));
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    incidente.setId(claves.getLong(1));
                }
            }
            return incidente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar incidente", e);
        }
    }

    private Incidente actualizar(Incidente incidente) {
        String sql = "UPDATE incidentes SET persona_id = ?, usuario_id = ?, descripcion = ?, gravedad = ?, fecha = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, incidente.getPersona().getId());
            stmt.setLong(2, incidente.getRegistradoPor().getId());
            stmt.setString(3, incidente.getDescripcion());
            stmt.setString(4, incidente.getGravedad().name());
            stmt.setTimestamp(5, Timestamp.valueOf(incidente.getFechaCreacion()));
            stmt.setLong(6, incidente.getId());
            stmt.executeUpdate();
            return incidente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar incidente", e);
        }
    }

    @Override
    public Optional<Incidente> buscarPorId(Long id) {
        String sql = construirSelectBase() + " WHERE i.id = ?";
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
            throw new RuntimeException("Error al buscar incidente por id", e);
        }
    }

    @Override
    public List<Incidente> listarTodos() {
        String sql = construirSelectBase() + " ORDER BY i.fecha DESC";
        List<Incidente> incidentes = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                incidentes.add(mapearFila(rs));
            }
            return incidentes;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar incidentes", e);
        }
    }

    @Override
    public List<Incidente> buscarPorPersona(Long personaId) {
        String sql = construirSelectBase() + " WHERE i.persona_id = ? ORDER BY i.fecha DESC";
        List<Incidente> incidentes = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    incidentes.add(mapearFila(rs));
                }
            }
            return incidentes;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar incidentes por persona", e);
        }
    }

    @Override
    public List<Incidente> buscarPorFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                            Long empresaId, GravedadIncidente gravedad) {
        StringBuilder sql = new StringBuilder(construirSelectBase());
        sql.append(" WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (fechaDesde != null) {
            sql.append(" AND i.fecha >= ? ");
            parametros.add(Timestamp.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append(" AND i.fecha <= ? ");
            parametros.add(Timestamp.valueOf(fechaHasta));
        }
        if (empresaId != null) {
            sql.append(" AND f.empresa_id = ? ");
            parametros.add(empresaId);
        }
        if (gravedad != null) {
            sql.append(" AND i.gravedad = ? ");
            parametros.add(gravedad.name());
        }
        sql.append(" ORDER BY i.fecha DESC");

        List<Incidente> incidentes = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    incidentes.add(mapearFila(rs));
                }
            }
            return incidentes;
        } catch (SQLException e) {
            throw new RuntimeException("Error al filtrar incidentes", e);
        }
    }

    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM incidentes WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar incidente", e);
        }
    }

    private String construirSelectBase() {
        return "SELECT i.id, i.persona_id, i.usuario_id, i.descripcion, i.gravedad, i.fecha, " +
               "p.documento AS persona_documento, p.nombre AS persona_nombre, " +
               "u.username AS usuario_username, u.nombre_completo AS usuario_nombre, " +
               "f.empresa_id AS funcionario_empresa_id " +
               "FROM incidentes i " +
               "INNER JOIN personas p ON i.persona_id = p.id " +
               "INNER JOIN usuarios u ON i.usuario_id = u.id " +
               "LEFT JOIN funcionarios f ON f.usuario_id = u.id";
    }

    private Incidente mapearFila(ResultSet rs) throws SQLException {
        Incidente incidente = new Incidente();
        incidente.setId(rs.getLong("id"));
        incidente.setDescripcion(rs.getString("descripcion"));
        incidente.setGravedad(GravedadIncidente.valueOf(rs.getString("gravedad")));
        incidente.setFechaCreacion(rs.getTimestamp("fecha").toLocalDateTime());

        Persona persona = new Persona();
        persona.setId(rs.getLong("persona_id"));
        persona.setDocumentoIdentidad(rs.getString("persona_documento"));
        persona.setNombreCompleto(rs.getString("persona_nombre"));
        incidente.setPersona(persona);

        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("usuario_id"));
        usuario.setUsername(rs.getString("usuario_username"));
        usuario.setNombreCompleto(rs.getString("usuario_nombre"));
        incidente.setRegistradoPor(usuario);

        Long empresaId = rs.getObject("funcionario_empresa_id", Long.class);
        if (empresaId != null) {
            Empresa empresa = new Empresa();
            empresa.setId(empresaId);
            incidente.setEmpresa(empresa);
        }

        return incidente;
    }
}