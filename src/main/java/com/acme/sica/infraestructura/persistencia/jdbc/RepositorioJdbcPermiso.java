package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.puerto.salida.PermisoRepositorioPuerto;

import java.sql.*;
import java.util.*;

/**
 * Implementación del patrón Repository sobre JDBC para la entidad Permiso.
 * Adaptador de salida que persiste y recupera datos desde la base de datos relacional.
 */
public class RepositorioJdbcPermiso implements PermisoRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcPermiso(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    /**
     * Persiste la entidad recibida en el almacén de datos.
     */
    @Override
    public Permiso guardar(Permiso permiso) {
        if (permiso.getId() == null) {
            return insertar(permiso);
        }
        return actualizar(permiso);
    }

    private Permiso insertar(Permiso permiso) {
        String sql = "INSERT INTO permisos (codigo, descripcion) VALUES (?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, permiso.getNombre());
            stmt.setString(2, permiso.getDescripcion());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    permiso.setId(claves.getLong(1));
                }
            }
            return permiso;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar permiso", e);
        }
    }

    private Permiso actualizar(Permiso permiso) {
        String sql = "UPDATE permisos SET codigo = ?, descripcion = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, permiso.getNombre());
            stmt.setString(2, permiso.getDescripcion());
            stmt.setLong(3, permiso.getId());
            stmt.executeUpdate();
            return permiso;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar permiso", e);
        }
    }

    /**
     * Busca una entidad por su identificador único.
     */
    @Override
    public Optional<Permiso> buscarPorId(Long id) {
        String sql = "SELECT id, codigo, descripcion FROM permisos WHERE id = ?";
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
            throw new RuntimeException("Error al buscar permiso por id", e);
        }
    }

    @Override
    public Optional<Permiso> buscarPorCodigo(String codigo) {
        String sql = "SELECT id, codigo, descripcion FROM permisos WHERE codigo = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar permiso por código", e);
        }
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Permiso> listarTodos() {
        String sql = "SELECT id, codigo, descripcion FROM permisos ORDER BY codigo";
        List<Permiso> permisos = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                permisos.add(mapearFila(rs));
            }
            return permisos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar permisos", e);
        }
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM permisos WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar permiso", e);
        }
    }

    @Override
    public boolean existePorCodigo(String codigo) {
        String sql = "SELECT 1 FROM permisos WHERE codigo = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar permiso por código", e);
        }
    }

    /**
     * Busca las entidades asociadas a un rol.
     */
    @Override
    public Set<Permiso> buscarPorRol(Long rolId) {
        String sql = "SELECT p.id, p.codigo, p.descripcion " +
                     "FROM permisos p " +
                     "INNER JOIN rol_permisos rp ON p.id = rp.permiso_id " +
                     "WHERE rp.rol_id = ?";
        Set<Permiso> permisos = new HashSet<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, rolId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permisos.add(mapearFila(rs));
                }
            }
            return permisos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar permisos por rol", e);
        }
    }

    @Override
    public Set<Permiso> buscarPorCodigos(Set<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return new HashSet<>();
        }
        String sql = "SELECT id, codigo, descripcion FROM permisos WHERE codigo IN (" +
                     String.join(",", Collections.nCopies(codigos.size(), "?")) + ")";
        Set<Permiso> permisos = new HashSet<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int indice = 1;
            for (String codigo : codigos) {
                stmt.setString(indice++, codigo);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permisos.add(mapearFila(rs));
                }
            }
            return permisos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar permisos por códigos", e);
        }
    }

    private Permiso mapearFila(ResultSet rs) throws SQLException {
        Permiso permiso = new Permiso();
        permiso.setId(rs.getLong("id"));
        permiso.setNombre(rs.getString("codigo"));
        permiso.setDescripcion(rs.getString("descripcion"));
        return permiso;
    }
}