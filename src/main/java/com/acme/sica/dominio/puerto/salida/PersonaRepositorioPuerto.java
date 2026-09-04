package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Persona;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida hexagonal que define las operaciones de persistencia
 * para la entidad Persona. Es implementado por los adaptadores de infraestructura.
 */
public interface PersonaRepositorioPuerto {

    Persona guardar(Persona persona);

    Optional<Persona> buscarPorId(Long id);

    Optional<Persona> buscarPorDocumento(String documento);

    List<Persona> listarTodos();

    List<Persona> listarBloqueadas();

    void eliminarPorId(Long id);

    boolean existePorDocumento(String documento);

    long contar();
}