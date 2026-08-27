package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.persona.CrearPersonaComando;
import com.acme.sica.aplicacion.persona.EditarPersonaComando;
import com.acme.sica.dominio.modelo.Persona;

import java.util.List;

/**
 * Puerto de entrada para la gestión de personas (HU-10).
 */
public interface GestionarPersonaCasoUso {

    Persona crear(CrearPersonaComando comando);

    Persona editar(EditarPersonaComando comando);

    void eliminar(Long personaId);

    List<Persona> listarTodos();

    Persona obtenerPorId(Long personaId);
}
