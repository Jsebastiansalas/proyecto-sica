package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.RegistrarTrabajadorCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

/**
 * Decorador de auditoría para el registro de trabajadores (HU-06).
 * Registra automáticamente CREAR_VISITA_TRABAJADOR en la bitácora.
 */
public class AuditoriaRegistrarTrabajadorDecorador implements RegistrarTrabajadorCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final RegistrarTrabajadorCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaRegistrarTrabajadorDecorador(RegistrarTrabajadorCasoUso decorado,
                                                 BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Visita registrar(RegistrarTrabajadorComando comando) {
        Visita visita = decorado.registrar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_VISITA_TRABAJADOR,
                ENTIDAD, visita.getId(),
                "Trabajador registrado: " + visita.getPersona().getNombreCompleto());
        return visita;
    }
}
