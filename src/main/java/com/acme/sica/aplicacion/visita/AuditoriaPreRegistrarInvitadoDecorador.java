package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.PreRegistrarInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

/**
 * Decorador de auditoría para pre-registro de invitados (HU-06).
 * Registra automáticamente CREAR_VISITA en la bitácora.
 */
public class AuditoriaPreRegistrarInvitadoDecorador implements PreRegistrarInvitadoCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final PreRegistrarInvitadoCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaPreRegistrarInvitadoDecorador(PreRegistrarInvitadoCasoUso decorado,
                                                  BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Pre-registra un invitado para una visita futura.
     */
    @Override
    public Visita preRegistrar(PreRegistrarInvitadoComando comando) {
        Visita visita = decorado.preRegistrar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_VISITA,
                ENTIDAD, visita.getId(), "Visita pre-registrada para: " + visita.getPersona().getNombreCompleto());
        return visita;
    }
}
