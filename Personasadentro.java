package com.acme.sica.infraestructura.persistencia;
import com.acme.sica.dominio.modelo.Activo;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.persistencia.jdbc.FabricaConexiones;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Personasadentro {
    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcVisita(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    /**
     * Persiste la entidad recibida en el almacén de datos.
     */
    @Override
    public Visita guardar(Visita visita) {
        if (visita.getId() == null) {
            return insertar(visita);
        }
        return actualizar(visita);
    }

    private Visita insertar(Visita visita) {
        String sql = "INSERT INTO visitas (persona_id, funcionario_id, empresa_id, registrado_por_id, " +
                     "fecha_hora_programada, fecha_hora_checkin, fecha_hora_checkout, estado, motivo, " +
                     "placa_vehicular, fecha_expiracion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, visita.getPersona().getId());
            stmt.setObject(2, visita.getFuncionario() != null ? visita.getFuncionario().getId() : null, Types.BIGINT);
            stmt.setObject(3, visita.getEmpresa() != null ? visita.getEmpresa().getId() : null, Types.BIGINT);
            stmt.setObject(4, visita.getRegistradaPor() != null ? visita.getRegistradaPor().getId() : null, Types.BIGINT);
            stmt.setTimestamp(5, visita.getFechaHoraEsperada() != null ? Timestamp.valueOf(visita.getFechaHoraEsperada()) : null);
            stmt.setTimestamp(6, visita.getFechaHoraIngreso() != null ? Timestamp.valueOf(visita.getFechaHoraIngreso()) : null);
            stmt.setTimestamp(7, visita.getFechaHoraSalida() != null ? Timestamp.valueOf(visita.getFechaHoraSalida()) : null);
            stmt.setString(8, visita.getEstado().name());
            stmt.setString(9, visita.getMotivo());
            stmt.setString(10, visita.getVehiculo() != null ? visita.getVehiculo().getPlaca() : null);
            stmt.setDate(11, visita.getFechaExpiracion() != null ? Date.valueOf(visita.getFechaExpiracion()) : null);
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    visita.setId(claves.getLong(1));
                }
            }
            guardarActivos(conn, visita);
            return visita;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar visita", e);
        }
    }

    private Visita actualizar(Visita visita) {
        String sql = "UPDATE visitas SET persona_id = ?, funcionario_id = ?, empresa_id = ?, " +
                     "registrado_por_id = ?, fecha_hora_programada = ?, " +
                     "fecha_hora_checkin = ?, fecha_hora_checkout = ?, estado = ?, motivo = ?, " +
                     "placa_vehicular = ?, fecha_expiracion = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, visita.getPersona().getId());
            stmt.setObject(2, visita.getFuncionario() != null ? visita.getFuncionario().getId() : null, Types.BIGINT);
            stmt.setObject(3, visita.getEmpresa() != null ? visita.getEmpresa().getId() : null, Types.BIGINT);
            stmt.setObject(4, visita.getRegistradaPor() != null ? visita.getRegistradaPor().getId() : null, Types.BIGINT);
            stmt.setTimestamp(5, visita.getFechaHoraEsperada() != null ? Timestamp.valueOf(visita.getFechaHoraEsperada()) : null);
            stmt.setTimestamp(6, visita.getFechaHoraIngreso() != null ? Timestamp.valueOf(visita.getFechaHoraIngreso()) : null);
            stmt.setTimestamp(7, visita.getFechaHoraSalida() != null ? Timestamp.valueOf(visita.getFechaHoraSalida()) : null);
            stmt.setString(8, visita.getEstado().name());
            stmt.setString(9, visita.getMotivo());
            stmt.setString(10, visita.getVehiculo() != null ? visita.getVehiculo().getPlaca() : null);
            stmt.setDate(11, visita.getFechaExpiracion() != null ? Date.valueOf(visita.getFechaExpiracion()) : null);
            stmt.setLong(12, visita.getId());
            stmt.executeUpdate();
            guardarActivos(conn, visita);
            return visita;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar visita", e);
        }
    }

    /**
     * Busca una entidad por su identificador único.
     */
    @Override
    public Optional<Visita> buscarPorId(Long id) {
        String sql = construirSelectBase() + " WHERE v.id = ?";
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
            throw new RuntimeException("Error al buscar visita por id", e);
        }
    }

    /**
     * Busca las entidades asociadas a una persona.
     */
    @Override
    public List<Visita> buscarPorPersona(Long personaId) {
        String sql = construirSelectBase() + " WHERE v.persona_id = ? ORDER BY v.fecha_creacion DESC";
        List<Visita> visitas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visitas.add(mapearFila(rs));
                }
            }
            return visitas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar visitas por persona", e);
        }
    }

    /**
     * Busca las entidades asociadas a una persona y en el estado indicado.
     */
    @Override
    public List<Visita> buscarPorPersonaYEstado(Long personaId, EstadoVisita estado) {
        String sql = construirSelectBase() + " WHERE v.persona_id = ? AND v.estado = ? ORDER BY v.fecha_creacion DESC";
        List<Visita> visitas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, personaId);
            stmt.setString(2, estado.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visitas.add(mapearFila(rs));
                }
            }
            return visitas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar visitas por persona y estado", e);
        }
    }

    /**
     * Busca una visita abierta asociada a una persona.
     */
    @Override
    public Optional<Visita> buscarVisitaAbiertaPorPersona(Long personaId) {
        String sql = construirSelectBase() + " WHERE v.persona_id = ? AND v.estado = 'DENTRO' ORDER BY v.fecha_creacion DESC LIMIT 1";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, personaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar visita abierta", e);
        }
    }

    /**
     * Busca las visitas pendientes de una empresa.
     */
    @Override
    public List<Visita> buscarPendientesPorEmpresa(Long empresaId) {
        String sql = construirSelectBase() + " WHERE f.empresa_id = ? AND v.estado IN ('PENDIENTE_APROBACION', 'PENDIENTE_APROBACION_OLVIDO') ORDER BY v.fecha_creacion DESC";
        List<Visita> visitas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visitas.add(mapearFila(rs));
                }
            }
            return visitas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar visitas pendientes por empresa", e);
        }
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Visita> listarTodos() {
        String sql = construirSelectBase() + " ORDER BY v.fecha_creacion DESC";
        List<Visita> visitas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                visitas.add(mapearFila(rs));
            }
            return visitas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar visitas", e);
        }
    }

    /**
     * Busca las entidades que coincidan con los filtros indicados.
     */
    @Override
    public List<Visita> buscarPorFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long empresaId, PuntoAcceso puntoAcceso) {
        StringBuilder sql = new StringBuilder(construirSelectBase());

        if (puntoAcceso != null) {
            sql.append(" INNER JOIN bitacora_auditoria ba ON ba.entidad = 'VISITA' AND ba.entidad_id = v.id AND ba.accion = 'CHECK_IN' ");
        }

        sql.append(" WHERE 1=1 ");
        List<Object> parametros = new ArrayList<>();

        if (fechaDesde != null) {
            sql.append(" AND v.fecha_hora_checkin >= ? ");
            parametros.add(Timestamp.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append(" AND v.fecha_hora_checkin <= ? ");
            parametros.add(Timestamp.valueOf(fechaHasta));
        }
        if (empresaId != null) {
            sql.append(" AND f.empresa_id = ? ");
            parametros.add(empresaId);
        }
        if (puntoAcceso != null) {
            sql.append(" AND ba.punto_acceso = ? ");
            parametros.add(puntoAcceso.name());
        }
        sql.append(" ORDER BY v.fecha_creacion DESC");

        List<Visita> visitas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    visitas.add(mapearFila(rs));
                }
            }
            return visitas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al filtrar visitas", e);
        }
    }

    private void guardarActivos(Connection conn, Visita visita) throws SQLException {
        if (visita.getId() == null) return;
        
        // Limpiar activos existentes
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM visita_activos WHERE visita_id = ?")) {
            stmt.setLong(1, visita.getId());
            stmt.executeUpdate();
        }
        
        if (visita.getActivos() == null || visita.getActivos().isEmpty()) return;
        
        // Insertar nuevos activos
        for (Activo activo : visita.getActivos()) {
            if (activo.getId() == null) {
                // Insertar el activo en la tabla de activos si es nuevo
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO activos (descripcion, numero_serie) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, activo.getDescripcion());
                    stmt.setString(2, activo.getNumeroSerie());
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            activo.setId(rs.getLong(1));
                        }
                    }
                }
            }
            // Vincular
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO visita_activos (visita_id, activo_id) VALUES (?, ?)")) {
                stmt.setLong(1, visita.getId());
                stmt.setLong(2, activo.getId());
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Retorna la cantidad de entidades pendientes.
     */
    @Override
    public long contarPendientes() {
        String sql = "SELECT COUNT(*) FROM visitas WHERE estado IN ('PENDIENTE_APROBACION', 'PENDIENTE_APROBACION_OLVIDO')";
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar visitas pendientes", e);
        }
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM visitas WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar visita", e);
        }
    }

    private String construirSelectBase() {
        return "SELECT v.id, v.persona_id, v.funcionario_id, v.empresa_id, v.registrado_por_id, " +
               "v.fecha_hora_programada, v.fecha_hora_checkin, v.fecha_hora_checkout, " +
               "v.estado, v.motivo, v.fecha_creacion, v.placa_vehicular, v.fecha_expiracion, " +
               "p.documento AS persona_documento, p.nombre AS persona_nombre, p.foto_url AS persona_foto_url, " +
               "p.tipo AS persona_tipo, p.bloqueada AS persona_bloqueada, " +
               "f.nombre AS funcionario_nombre, f.empresa_id AS funcionario_empresa_id, " +
               "e.nombre AS empresa_nombre, e.ubicacion AS empresa_ubicacion, e.activa AS empresa_activa, " +
               "u.username AS registrado_por_username, u.nombre_completo AS registrado_por_nombre " +
               "FROM visitas v " +
               "INNER JOIN personas p ON v.persona_id = p.id " +
               "LEFT JOIN funcionarios f ON v.funcionario_id = f.id " +
               "LEFT JOIN empresas e ON f.empresa_id = e.id " +
               "LEFT JOIN usuarios u ON v.registrado_por_id = u.id";
    }

    private Visita mapearFila(ResultSet rs) throws SQLException {
        Visita visita = new Visita();
        visita.setId(rs.getLong("id"));
        visita.setFechaHoraEsperada(rs.getTimestamp("fecha_hora_programada") != null ?
                rs.getTimestamp("fecha_hora_programada").toLocalDateTime() : null);
        visita.setFechaHoraIngreso(rs.getTimestamp("fecha_hora_checkin") != null ?
                rs.getTimestamp("fecha_hora_checkin").toLocalDateTime() : null);
        visita.setFechaHoraSalida(rs.getTimestamp("fecha_hora_checkout") != null ?
                rs.getTimestamp("fecha_hora_checkout").toLocalDateTime() : null);
        visita.setEstado(EstadoVisita.valueOf(rs.getString("estado")));
        visita.setMotivo(rs.getString("motivo"));
        visita.setFechaExpiracion(rs.getDate("fecha_expiracion") != null ?
                rs.getDate("fecha_expiracion").toLocalDate() : null);
        visita.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        
        String placaVehicular = rs.getString("placa_vehicular");
        if (placaVehicular != null) {
            com.acme.sica.dominio.modelo.Vehiculo vehiculo = new com.acme.sica.dominio.modelo.Vehiculo();
            vehiculo.setPlaca(placaVehicular);
            visita.setVehiculo(vehiculo);
        }

        Persona persona = new Persona();
        persona.setId(rs.getLong("persona_id"));
        persona.setDocumentoIdentidad(rs.getString("persona_documento"));
        persona.setNombreCompleto(rs.getString("persona_nombre"));
        persona.setFotoUrl(rs.getString("persona_foto_url"));
        persona.setTipo(TipoPersona.valueOf(rs.getString("persona_tipo")));
        persona.setBloqueada(rs.getBoolean("persona_bloqueada"));
        visita.setPersona(persona);

        Long funcionarioId = rs.getObject("funcionario_id", Long.class);
        if (funcionarioId != null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setId(funcionarioId);
            funcionario.setNombreCompleto(rs.getString("funcionario_nombre"));
            Long empresaId = rs.getObject("funcionario_empresa_id", Long.class);
            if (empresaId != null) {
                Empresa empresa = new Empresa();
                empresa.setId(empresaId);
                empresa.setNombre(rs.getString("empresa_nombre"));
                empresa.setUbicacion(rs.getString("empresa_ubicacion"));
                empresa.setActiva(rs.getBoolean("empresa_activa"));
                funcionario.setEmpresa(empresa);
            }
            visita.setFuncionario(funcionario);
        }

        Long empresaVisitaId = rs.getObject("empresa_id", Long.class);
        if (empresaVisitaId != null) {
            Empresa empresaVisita = new Empresa();
            empresaVisita.setId(empresaVisitaId);
            empresaVisita.setNombre(rs.getString("empresa_nombre"));
            empresaVisita.setUbicacion(rs.getString("empresa_ubicacion"));
            empresaVisita.setActiva(rs.getBoolean("empresa_activa"));
            visita.setEmpresa(empresaVisita);
        }

        Long registradoPorId = rs.getObject("registrado_por_id", Long.class);
        if (registradoPorId != null) {
            com.acme.sica.dominio.modelo.Usuario registradoPor = new com.acme.sica.dominio.modelo.Usuario();
            registradoPor.setId(registradoPorId);
            registradoPor.setUsername(rs.getString("registrado_por_username"));
            registradoPor.setNombreCompleto(rs.getString("registrado_por_nombre"));
            visita.setRegistradaPor(registradoPor);
        }

        cargarActivosParaVisita(visita);

        return visita;
    }

    private void cargarActivosParaVisita(Visita visita) {
        String sql = "SELECT a.id, a.descripcion, a.numero_serie FROM activos a " +
                     "INNER JOIN visita_activos va ON a.id = va.activo_id " +
                     "WHERE va.visita_id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, visita.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Activo activo = new Activo(rs.getString("descripcion"), rs.getString("numero_serie"));
                    activo.setId(rs.getLong("id"));
                    visita.agregarActivo(activo);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar activos de visita", e);
        }
    }
}