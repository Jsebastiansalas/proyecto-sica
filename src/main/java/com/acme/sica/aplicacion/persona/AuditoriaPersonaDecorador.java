package com.acme.sica.aplicacion.persona;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarPersonaCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para la gestión de personas (HU-06).
 * Registra automáticamente CREAR/EDITAR/ELIMINAR persona en la bitácora.
 */
public class AuditoriaPersonaDecorador implements GestionarPersonaCasoUso {

    private static final String ENTIDAD = "PERSONA";

    private final GestionarPersonaCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaPersonaDecorador(GestionarPersonaCasoUso decorado,
                                     BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Persona crear(CrearPersonaComando comando) {
        Persona persona = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_PERSONA,
                ENTIDAD, persona.getId(), "Persona creada: " + persona.getNombreCompleto());
        return persona;
    }

    @Override
    public Persona editar(EditarPersonaComando comando) {
        Persona persona = decorado.editar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.EDITAR_PERSONA,
                ENTIDAD, persona.getId(), "Persona editada: " + persona.getNombreCompleto());
        return persona;
    }

    @Override
    public void eliminar(Long personaId) {
        decorado.eliminar(personaId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ELIMINAR_PERSONA,
                ENTIDAD, personaId, "Persona eliminada: id=" + personaId);
    }

    @Override
    public List<Persona> listarTodos() {
        return decorado.listarTodos();
    }

    @Override
    public Persona obtenerPorId(Long personaId) {
        return decorado.obtenerPorId(personaId);
    }
}
