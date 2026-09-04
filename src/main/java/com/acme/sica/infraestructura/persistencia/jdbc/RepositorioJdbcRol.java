package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementación del patrón Repository sobre JDBC para la entidad Rol.
 * Adaptador de salida que persiste y recupera datos desde la base de datos relacional.
 */
public class RepositorioJdbcRol implements RolRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcRol(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    /**
     * Persiste la entidad recibida en el almacén de datos.
     */
    @Override
    public Rol guardar(Rol rol) {
        if (rol.getId() == null) {
            return insertar(rol);
        }
        return actualizar(rol);
    }

    private Rol insertar(Rol rol) {
        String sql = "INSERT INTO roles (nombre, descripcion, activo) VALUES (?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, rol.getNombre());
            stmt.setString(2, rol.getDescripcion());
            stmt.setBoolean(3, rol.isActivo());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    rol.setId(claves.getLong(1));
                }
            }
            return rol;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar rol", e);
        }
    }

    private Rol actualizar(Rol rol) {
        String sql = "UPDATE roles SET nombre = ?, descripcion = ?, activo = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rol.getNombre());
            stmt.setString(2, rol.getDescripcion());
            stmt.setBoolean(3, rol.isActivo());
            stmt.setLong(4, rol.getId());
            stmt.executeUpdate();
            return rol;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar rol", e);
        }
    }

    /**
     * Busca una entidad por su identificador único.
     */
    @Override
    public Optional<Rol> buscarPorId(Long id) {
        String sql = "SELECT id, nombre, descripcion, activo FROM roles WHERE id = ?";
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
            throw new RuntimeException("Error al buscar rol por id", e);
        }
    }

    /**
     * Busca una entidad por su nombre.
     */
    @Override
    public Optional<Rol> buscarPorNombre(String nombre) {
        String sql = "SELECT id, nombre, descripcion, activo FROM roles WHERE nombre = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar rol por nombre", e);
        }
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Rol> listarTodos() {
        String sql = "SELECT id, nombre, descripcion, activo FROM roles ORDER BY nombre";
        List<Rol> roles = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roles.add(mapearFila(rs));
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar roles", e);
        }
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM roles WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar rol", e);
        }
    }

    /**
     * Verifica si ya existe una entidad con el nombre indicado.
     */
    @Override
    public boolean existePorNombre(String nombre) {
        String sql = "SELECT 1 FROM roles WHERE nombre = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar rol por nombre", e);
        }
    }

    /**
     * Cuenta cuántos usuarios tienen asignado el rol indicado.
     */
    @Override
    public long contarUsuariosConRol(Long rolId) {
        String sql = "SELECT COUNT(*) FROM usuario_roles WHERE rol_id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rolId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar usuarios por rol", e);
        }
    }

    /**
     * Retorna la cantidad total de entidades registradas.
     */
    @Override
    public long contar() {
        String sql = "SELECT COUNT(*) FROM roles";
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar roles", e);
        }
    }

    /**
     * Obtiene los identificadores de permisos asociados a un rol.
     */
    @Override
    public Set<Long> buscarIdsPermisosPorRol(Long rolId) {
        String sql = "SELECT permiso_id FROM rol_permisos WHERE rol_id = ?";
        Set<Long> ids = new HashSet<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rolId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("permiso_id"));
                }
            }
            return ids;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar permisos del rol", e);
        }
    }

    /**
     * Asigna los permisos indicados a un rol.
     */
    @Override
    public void asignarPermisos(Long rolId, Set<Long> permisoIds) {
        eliminarPermisos(rolId);
        if (permisoIds == null || permisoIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO rol_permisos (rol_id, permiso_id) VALUES (?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Long permisoId : permisoIds) {
                stmt.setLong(1, rolId);
                stmt.setLong(2, permisoId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar permisos al rol", e);
        }
    }

    /**
     * Elimina los permisos asignados a un rol.
     */
    @Override
    public void eliminarPermisos(Long rolId) {
        String sql = "DELETE FROM rol_permisos WHERE rol_id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rolId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar permisos del rol", e);
        }
    }

    private Rol mapearFila(ResultSet rs) throws SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getLong("id"));
        rol.setNombre(rs.getString("nombre"));
        rol.setDescripcion(rs.getString("descripcion"));
        rol.setActivo(rs.getBoolean("activo"));
        return rol;
    }
}