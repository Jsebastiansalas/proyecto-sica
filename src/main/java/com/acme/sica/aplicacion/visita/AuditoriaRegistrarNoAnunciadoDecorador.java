package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.RegistrarNoAnunciadoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

/**
 * Decorador de auditoría para el registro de invitados no anunciados (HU-06).
 * Registra automáticamente CREAR_VISITA_NO_ANUNCIADO en la bitácora.
 */
public class AuditoriaRegistrarNoAnunciadoDecorador implements RegistrarNoAnunciadoCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final RegistrarNoAnunciadoCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaRegistrarNoAnunciadoDecorador(RegistrarNoAnunciadoCasoUso decorado,
                                                  BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Visita registrar(RegistrarNoAnunciadoComando comando) {
        Visita visita = decorado.registrar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_VISITA_NO_ANUNCIADO,
                ENTIDAD, visita.getId(),
                "Visita no anunciada registrada: " + visita.getPersona().getNombreCompleto());
        return visita;
    }
}
