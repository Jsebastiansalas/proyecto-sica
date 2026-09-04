package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.CheckInInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para el check-in de invitados (HU-06).
 * Registra automáticamente CHECK_IN en la bitácora.
 */
public class AuditoriaCheckInDecorador implements CheckInInvitadoCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final CheckInInvitadoCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaCheckInDecorador(CheckInInvitadoCasoUso decorado,
                                     BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Registra el ingreso de un invitado.
     */
    @Override
    public Visita checkIn(CheckInInvitadoComando comando) {
        Visita visita = decorado.checkIn(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CHECK_IN,
                ENTIDAD, visita.getId(),
                "Check-in de: " + visita.getPersona().getNombreCompleto());
        return visita;
    }

    @Override
    public List<Visita> listarAprobadas() {
        return decorado.listarAprobadas();
    }

    @Override
    public List<Visita> listarDentro() {
        return decorado.listarDentro();
    }
}
