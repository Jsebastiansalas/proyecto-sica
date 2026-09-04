package com.acme.sica.aplicacion.persona;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarBloqueoPersonaCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría que envuelve el caso de uso de bloqueo persona
 * y registra las acciones realizadas en la bitácora de auditoría.
 */
public class AuditoriaBloqueoPersonaDecorador implements GestionarBloqueoPersonaCasoUso {

    private final GestionarBloqueoPersonaCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaBloqueoPersonaDecorador(GestionarBloqueoPersonaCasoUso decorado,
                                            BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Bloquea una persona en el sistema.
     */
    @Override
    public Persona bloquear(BloquearPersonaComando comando) {
        Persona persona = decorado.bloquear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.BLOQUEAR_PERSONA,
                "PERSONA", persona.getId(), "Persona bloqueada: " + persona.getNombreCompleto()
                        + ". Motivo: " + persona.getMotivoBloqueo());
        return persona;
    }

    /**
     * Desbloquea una persona previamente bloqueada.
     */
    @Override
    public Persona desbloquear(Long personaId) {
        Persona persona = decorado.desbloquear(personaId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.DESBLOQUEAR_PERSONA,
                "PERSONA", persona.getId(), "Persona desbloqueada: " + persona.getNombreCompleto());
        return persona;
    }

    @Override
    public List<Persona> listarTodas() {
        return decorado.listarTodas();
    }

    /**
     * Obtiene el listado de entidades bloqueadas.
     */
    @Override
    public List<Persona> listarBloqueadas() {
        return decorado.listarBloqueadas();
    }
}
