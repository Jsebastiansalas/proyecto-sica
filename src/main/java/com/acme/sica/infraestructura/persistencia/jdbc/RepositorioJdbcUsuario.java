package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;

import java.sql.*;
import java.util.*;

public class RepositorioJdbcUsuario implements UsuarioRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcUsuario(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        if (usuario.getId() == null) {
            return insertar(usuario);
        }
        return actualizar(usuario);
    }

    private Usuario insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (username, password_hash, nombre_completo, activo) VALUES (?, ?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getNombreCompleto());
            stmt.setBoolean(4, usuario.isActivo());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    usuario.setId(claves.getLong(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario", e);
        }
    }

    private Usuario actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET username = ?, password_hash = ?, nombre_completo = ?, activo = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getNombreCompleto());
            stmt.setBoolean(4, usuario.isActivo());
            stmt.setLong(5, usuario.getId());
            stmt.executeUpdate();
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT id, username, password_hash, nombre_completo, activo, fecha_creacion FROM usuarios WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapearFila(rs);
                    cargarRolesYPermisos(conn, usuario);
                    return Optional.of(usuario);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por id", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT id, username, password_hash, nombre_completo, activo, fecha_creacion FROM usuarios WHERE username = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapearFila(rs);
                    cargarRolesYPermisos(conn, usuario);
                    return Optional.of(usuario);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario por username", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT id, username, password_hash, nombre_completo, activo, fecha_creacion FROM usuarios ORDER BY username";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = mapearFila(rs);
                cargarRolesYPermisos(conn, usuario);
                usuarios.add(usuario);
            }
            return usuarios;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }
    }

    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    @Override
    public boolean existePorUsername(String username) {
        String sql = "SELECT 1 FROM usuarios WHERE username = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar usuario por username", e);
        }
    }

    @Override
    public long contarUsuariosPorRol(Long rolId) {
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

    @Override
    public long contar() {
        String sql = "SELECT COUNT(*) FROM usuarios";
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar usuarios", e);
        }
    }

    public void asignarRoles(Long usuarioId, Set<Long> rolIds) {
        eliminarRoles(usuarioId);
        if (rolIds == null || rolIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO usuario_roles (usuario_id, rol_id) VALUES (?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Long rolId : rolIds) {
                stmt.setLong(1, usuarioId);
                stmt.setLong(2, rolId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asignar roles al usuario", e);
        }
    }

    public void eliminarRoles(Long usuarioId) {
        String sql = "DELETE FROM usuario_roles WHERE usuario_id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar roles del usuario", e);
        }
    }

    private void cargarRolesYPermisos(Connection conn, Usuario usuario) throws SQLException {
        Set<Rol> roles = new HashSet<>();
        String sqlRoles = "SELECT r.id, r.nombre, r.descripcion FROM roles r " +
                          "INNER JOIN usuario_roles ur ON r.id = ur.rol_id WHERE ur.usuario_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlRoles)) {
            stmt.setLong(1, usuario.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Rol rol = new Rol();
                    rol.setId(rs.getLong("id"));
                    rol.setNombre(rs.getString("nombre"));
                    rol.setDescripcion(rs.getString("descripcion"));
                    roles.add(rol);
                }
            }
        }

        for (Rol rol : roles) {
            Set<Permiso> permisos = new HashSet<>();
            String sqlPermisos = "SELECT p.id, p.codigo, p.descripcion FROM permisos p " +
                                 "INNER JOIN rol_permisos rp ON p.id = rp.permiso_id WHERE rp.rol_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPermisos)) {
                stmt.setLong(1, rol.getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Permiso permiso = new Permiso();
                        permiso.setId(rs.getLong("id"));
                        permiso.setNombre(rs.getString("codigo"));
                        permiso.setDescripcion(rs.getString("descripcion"));
                        permisos.add(permiso);
                    }
                }
            }
            rol.setPermisos(permisos);
        }

        usuario.setRoles(roles);
    }

    private Usuario mapearFila(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPassword(rs.getString("password_hash"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        return usuario;
    }
}