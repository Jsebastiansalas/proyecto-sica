package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.incidente.CrearIncidenteComando;
import com.acme.sica.dominio.modelo.Incidente;

import java.util.List;

/**
 * Puerto de entrada hexagonal que define el caso de uso para gestionar incidentes.
 * Es implementado por la capa de aplicación.
 */
public interface GestionarIncidenteCasoUso {

    Incidente crear(CrearIncidenteComando comando);

    List<Incidente> listarTodos();

    List<com.acme.sica.dominio.modelo.Persona> listarPersonas();
}
