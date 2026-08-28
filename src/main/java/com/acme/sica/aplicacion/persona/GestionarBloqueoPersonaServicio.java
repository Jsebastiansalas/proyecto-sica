package com.acme.sica.aplicacion.persona;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.puerto.entrada.GestionarBloqueoPersonaCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

public class GestionarBloqueoPersonaServicio implements GestionarBloqueoPersonaCasoUso {

    private static final String PERMISO_REQUERIDO = "bloquear_persona";

    private final PersonaRepositorioPuerto personaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarBloqueoPersonaServicio(PersonaRepositorioPuerto personaRepositorio,
                                           ManejadorAutorizacion cadenaAutorizacion) {
        this.personaRepositorio = personaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Persona bloquear(BloquearPersonaComando comando) {
        autorizar("bloquear persona");
        if (comando.getPersonaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una persona");
        }
        if (comando.getMotivo() == null || comando.getMotivo().isBlank()) {
            throw new IllegalArgumentException("El motivo del bloqueo es obligatorio");
        }

        Persona persona = buscar(comando.getPersonaId());
        if (persona.isBloqueada()) {
            throw new IllegalStateException("La persona ya está bloqueada");
        }
        persona.bloquear(comando.getMotivo().trim());
        return personaRepositorio.guardar(persona);
    }

    @Override
    public Persona desbloquear(Long personaId) {
        autorizar("desbloquear persona");
        Persona persona = buscar(personaId);
        if (!persona.isBloqueada()) {
            throw new IllegalStateException("La persona no está bloqueada");
        }
        persona.desbloquear();
        return personaRepositorio.guardar(persona);
    }

    @Override
    public List<Persona> listarTodas() {
        autorizar("listar personas para bloqueo");
        return personaRepositorio.listarTodos();
    }

    @Override
    public List<Persona> listarBloqueadas() {
        autorizar("listar personas bloqueadas");
        return personaRepositorio.listarBloqueadas();
    }

    private Persona buscar(Long personaId) {
        return personaRepositorio.buscarPorId(personaId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Persona no encontrada con id " + personaId));
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
