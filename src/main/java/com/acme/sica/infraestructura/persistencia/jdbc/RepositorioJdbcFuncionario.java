package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepositorioJdbcFuncionario implements FuncionarioRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcFuncionario(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    @Override
    public Funcionario guardar(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            return insertar(funcionario);
        }
        return actualizar(funcionario);
    }

    private Funcionario insertar(Funcionario funcionario) {
        String sql = "INSERT INTO funcionarios (usuario_id, empresa_id, nombre, cargo, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, funcionario.getUsuario() != null ? funcionario.getUsuario().getId() : null, Types.BIGINT);
            stmt.setLong(2, funcionario.getEmpresa().getId());
            stmt.setString(3, funcionario.getNombreCompleto());
            stmt.setString(4, funcionario.getCargo());
            stmt.setBoolean(5, funcionario.isActivo());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    funcionario.setId(claves.getLong(1));
                }
            }
            return funcionario;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar funcionario", e);
        }
    }

    private Funcionario actualizar(Funcionario funcionario) {
        String sql = "UPDATE funcionarios SET usuario_id = ?, empresa_id = ?, nombre = ?, cargo = ?, activo = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, funcionario.getUsuario() != null ? funcionario.getUsuario().getId() : null, Types.BIGINT);
            stmt.setLong(2, funcionario.getEmpresa().getId());
            stmt.setString(3, funcionario.getNombreCompleto());
            stmt.setString(4, funcionario.getCargo());
            stmt.setBoolean(5, funcionario.isActivo());
            stmt.setLong(6, funcionario.getId());
            stmt.executeUpdate();
            return funcionario;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar funcionario", e);
        }
    }

    @Override
    public Optional<Funcionario> buscarPorId(Long id) {
        String sql = construirSelectBase() + " WHERE f.id = ?";
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
            throw new RuntimeException("Error al buscar funcionario por id", e);
        }
    }

    @Override
    public Optional<Funcionario> buscarPorUsuario(Long usuarioId) {
        String sql = construirSelectBase() + " WHERE f.usuario_id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFila(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar funcionario por usuario", e);
        }
    }

    @Override
    public List<Funcionario> buscarPorEmpresa(Long empresaId) {
        String sql = construirSelectBase() + " WHERE f.empresa_id = ? ORDER BY f.nombre";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    funcionarios.add(mapearFila(rs));
                }
            }
            return funcionarios;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar funcionarios por empresa", e);
        }
    }

    @Override
    public List<Funcionario> listarTodos() {
        String sql = construirSelectBase() + " ORDER BY f.nombre";
        List<Funcionario> funcionarios = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                funcionarios.add(mapearFila(rs));
            }
            return funcionarios;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar funcionarios", e);
        }
    }

    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM funcionarios WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar funcionario", e);
        }
    }

    private String construirSelectBase() {
        return "SELECT f.id, f.usuario_id, f.empresa_id, f.nombre, f.cargo, f.activo, " +
               "e.id AS empresa_id, e.nombre AS empresa_nombre, e.ubicacion AS empresa_ubicacion, e.activa AS empresa_activa " +
               "FROM funcionarios f " +
               "INNER JOIN empresas e ON f.empresa_id = e.id";
    }

    private Funcionario mapearFila(ResultSet rs) throws SQLException {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getLong("id"));
        funcionario.setNombreCompleto(rs.getString("nombre"));
        funcionario.setCargo(rs.getString("cargo"));
        funcionario.setActivo(rs.getBoolean("activo"));

        Long usuarioId = rs.getObject("usuario_id", Long.class);
        if (usuarioId != null) {
            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);
            funcionario.setUsuario(usuario);
        }

        Empresa empresa = new Empresa();
        empresa.setId(rs.getLong("empresa_id"));
        empresa.setNombre(rs.getString("empresa_nombre"));
        empresa.setUbicacion(rs.getString("empresa_ubicacion"));
        empresa.setActiva(rs.getBoolean("empresa_activa"));
        funcionario.setEmpresa(empresa);

        return funcionario;
    }
}