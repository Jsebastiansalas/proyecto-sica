package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioJdbcBitacora implements BitacoraRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcBitacora(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    @Override
    public BitacoraAuditoria guardar(BitacoraAuditoria bitacora) {
        String sql = "INSERT INTO bitacora_auditoria (usuario_id, usuario_username, accion, entidad, entidad_id, detalle, fecha) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, bitacora.getUsuarioId(), Types.BIGINT);
            stmt.setString(2, bitacora.getUsuarioNombre());
            stmt.setString(3, bitacora.getAccion());
            stmt.setString(4, bitacora.getEntidad());
            stmt.setObject(5, bitacora.getEntidadId(), Types.BIGINT);
            stmt.setString(6, bitacora.getDetalles());
            stmt.setTimestamp(7, Timestamp.valueOf(bitacora.getFechaHora()));
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    bitacora.setId(claves.getLong(1));
                }
            }
            return bitacora;
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar en bitácora", e);
        }
    }

    @Override
    public List<BitacoraAuditoria> listarTodos() {
        String sql = "SELECT id, usuario_id, usuario_username, accion, entidad, entidad_id, detalle, fecha " +
                     "FROM bitacora_auditoria ORDER BY fecha DESC";
        List<BitacoraAuditoria> registros = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                registros.add(mapearFila(rs));
            }
            return registros;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar bitácora", e);
        }
    }

    @Override
    public List<BitacoraAuditoria> buscarPorFiltros(Long usuarioId, String entidad,
                                                    LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        StringBuilder sql = new StringBuilder(
                "SELECT id, usuario_id, usuario_username, accion, entidad, entidad_id, detalle, fecha " +
                "FROM bitacora_auditoria WHERE 1=1 "
        );
        List<Object> parametros = new ArrayList<>();

        if (usuarioId != null) {
            sql.append(" AND usuario_id = ? ");
            parametros.add(usuarioId);
        }
        if (entidad != null && !entidad.isBlank()) {
            sql.append(" AND entidad = ? ");
            parametros.add(entidad);
        }
        if (fechaDesde != null) {
            sql.append(" AND fecha >= ? ");
            parametros.add(Timestamp.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append(" AND fecha <= ? ");
            parametros.add(Timestamp.valueOf(fechaHasta));
        }
        sql.append(" ORDER BY fecha DESC");

        List<BitacoraAuditoria> registros = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registros.add(mapearFila(rs));
                }
            }
            return registros;
        } catch (SQLException e) {
            throw new RuntimeException("Error al filtrar bitácora", e);
        }
    }

    private BitacoraAuditoria mapearFila(ResultSet rs) throws SQLException {
        BitacoraAuditoria bitacora = new BitacoraAuditoria();
        bitacora.setId(rs.getLong("id"));
        bitacora.setUsuarioId(rs.getObject("usuario_id", Long.class));
        bitacora.setUsuarioNombre(rs.getString("usuario_username"));
        bitacora.setAccion(rs.getString("accion"));
        bitacora.setEntidad(rs.getString("entidad"));
        bitacora.setEntidadId(rs.getObject("entidad_id", Long.class));
        bitacora.setDetalles(rs.getString("detalle"));
        bitacora.setFechaHora(rs.getTimestamp("fecha").toLocalDateTime());
        return bitacora;
    }
}