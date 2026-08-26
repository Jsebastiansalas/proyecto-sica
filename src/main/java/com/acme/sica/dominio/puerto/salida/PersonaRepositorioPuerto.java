package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Persona;

import java.util.List;
import java.util.Optional;

public interface PersonaRepositorioPuerto {

    Persona guardar(Persona persona);

    Optional<Persona> buscarPorId(Long id);

    Optional<Persona> buscarPorDocumento(String documento);

    List<Persona> listarTodos();

    List<Persona> listarBloqueadas();

    void eliminarPorId(Long id);

    boolean existePorDocumento(String documento);
}