package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.persona.BloquearPersonaComando;
import com.acme.sica.dominio.modelo.Persona;

import java.util.List;

/**
 * Puerto de entrada hexagonal que define el caso de uso para gestionar el bloqueo de personas.
 * Es implementado por la capa de aplicación.
 */
public interface GestionarBloqueoPersonaCasoUso {

    Persona bloquear(BloquearPersonaComando comando);

    Persona desbloquear(Long personaId);

    List<Persona> listarTodas();

    List<Persona> listarBloqueadas();
}
