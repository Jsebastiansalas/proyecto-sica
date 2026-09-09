package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Vehiculo;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para acceder a la persistencia de los vehículos.
 */
public interface VehiculoRepositorioPuerto {

    /**
     * Guarda un vehículo en el repositorio.
     * @param vehiculo El vehículo a guardar.
     * @return El vehículo guardado.
     */
    Vehiculo guardar(Vehiculo vehiculo);

    /**
     * Busca un vehículo por su placa.
     * @param placa La placa del vehículo a buscar.
     * @return El vehículo encontrado, si existe.
     */
    Optional<Vehiculo> buscarPorPlaca(String placa);

    /**
     * Obtiene el listado completo de vehículos registrados (control de flota).
     * @return Lista de vehículos ordenados por marca y placa.
     */
    List<Vehiculo> listarTodos();
}
