package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.puerto.salida.EmpresaRepositorioPuerto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del patrón Repository sobre JDBC para la entidad Empresa.
 * Adaptador de salida que persiste y recupera datos desde la base de datos relacional.
 */
public class RepositorioJdbcEmpresa implements EmpresaRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcEmpresa(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    /**
     * Persiste la entidad recibida en el almacén de datos.
     */
    @Override
    public Empresa guardar(Empresa empresa) {
        if (empresa.getId() == null) {
            return insertar(empresa);
        }
        return actualizar(empresa);
    }

    private Empresa insertar(Empresa empresa) {
        String sql = "INSERT INTO empresas (nombre, ubicacion, activa) VALUES (?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getUbicacion());
            stmt.setBoolean(3, empresa.isActiva());
            stmt.executeUpdate();

            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) {
                    empresa.setId(claves.getLong(1));
                }
            }
            return empresa;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar empresa", e);
        }
    }

    private Empresa actualizar(Empresa empresa) {
        String sql = "UPDATE empresas SET nombre = ?, ubicacion = ?, activa = ? WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getUbicacion());
            stmt.setBoolean(3, empresa.isActiva());
            stmt.setLong(4, empresa.getId());
            stmt.executeUpdate();
            return empresa;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar empresa", e);
        }
    }

    /**
     * Busca una entidad por su identificador único.
     */
    @Override
    public Optional<Empresa> buscarPorId(Long id) {
        String sql = "SELECT id, nombre, ubicacion, activa FROM empresas WHERE id = ?";
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
            throw new RuntimeException("Error al buscar empresa por id", e);
        }
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Empresa> listarTodos() {
        String sql = "SELECT id, nombre, ubicacion, activa FROM empresas ORDER BY nombre";
        List<Empresa> empresas = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(mapearFila(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar empresas", e);
        }
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminarPorId(Long id) {
        String sql = "DELETE FROM empresas WHERE id = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar empresa", e);
        }
    }

    /**
     * Verifica si ya existe una entidad con el nombre indicado.
     */
    @Override
    public boolean existePorNombre(String nombre) {
        String sql = "SELECT 1 FROM empresas WHERE nombre = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar empresa por nombre", e);
        }
    }

    /**
     * Retorna la cantidad total de entidades registradas.
     */
    @Override
    public long contar() {
        String sql = "SELECT COUNT(*) FROM empresas";
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar empresas", e);
        }
    }

    private Empresa mapearFila(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(rs.getLong("id"));
        empresa.setNombre(rs.getString("nombre"));
        empresa.setUbicacion(rs.getString("ubicacion"));
        empresa.setActiva(rs.getBoolean("activa"));
        return empresa;
    }
}