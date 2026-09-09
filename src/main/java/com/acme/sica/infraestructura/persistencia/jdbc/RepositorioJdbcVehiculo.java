package com.acme.sica.infraestructura.persistencia.jdbc;

import com.acme.sica.dominio.modelo.Vehiculo;
import com.acme.sica.dominio.puerto.salida.VehiculoRepositorioPuerto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC para el repositorio de Vehículos.
 */
public class RepositorioJdbcVehiculo implements VehiculoRepositorioPuerto {

    private final FabricaConexiones fabricaConexiones;

    public RepositorioJdbcVehiculo(FabricaConexiones fabricaConexiones) {
        this.fabricaConexiones = fabricaConexiones;
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        Optional<Vehiculo> existente = buscarPorPlaca(vehiculo.getPlaca());
        if (existente.isPresent()) {
            return actualizar(vehiculo);
        }
        return insertar(vehiculo);
    }

    private Vehiculo insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (placa, marca, tipo) VALUES (?, ?, ?)";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehiculo.getPlaca());
            stmt.setString(2, vehiculo.getMarca());
            stmt.setString(3, vehiculo.getTipo());
            stmt.executeUpdate();

            return vehiculo;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar vehículo", e);
        }
    }

    private Vehiculo actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculos SET marca = ?, tipo = ? WHERE placa = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehiculo.getMarca());
            stmt.setString(2, vehiculo.getTipo());
            stmt.setString(3, vehiculo.getPlaca());
            stmt.executeUpdate();

            return vehiculo;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar vehículo", e);
        }
    }

    @Override
    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        String sql = "SELECT placa, marca, tipo FROM vehiculos WHERE placa = ?";
        try (Connection conn = fabricaConexiones.crearConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vehiculo vehiculo = new Vehiculo(
                            rs.getString("placa"),
                            rs.getString("marca"),
                            rs.getString("tipo")
                    );
                    return Optional.of(vehiculo);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar vehículo por placa", e);
        }
    }

    @Override
    public List<Vehiculo> listarTodos() {
        String sql = "SELECT placa, marca, tipo FROM vehiculos ORDER BY marca, placa";
        List<Vehiculo> vehiculos = new ArrayList<>();
        try (Connection conn = fabricaConexiones.crearConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vehiculos.add(new Vehiculo(
                        rs.getString("placa"),
                        rs.getString("marca"),
                        rs.getString("tipo")
                ));
            }
            return vehiculos;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar vehículos", e);
        }
    }
}
