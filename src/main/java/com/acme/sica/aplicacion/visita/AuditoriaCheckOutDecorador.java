package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.CheckOutCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para el check-out (HU-06).
 * Registra automáticamente CHECK_OUT en la bitácora.
 */
public class AuditoriaCheckOutDecorador implements CheckOutCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final CheckOutCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaCheckOutDecorador(CheckOutCasoUso decorado,
                                      BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Visita checkOut(CheckOutComando comando) {
        Visita visita = decorado.checkOut(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CHECK_OUT,
                ENTIDAD, visita.getId(),
                "Check-out de: " + visita.getPersona().getNombreCompleto());
        return visita;
    }

    @Override
    public List<Visita> listarDentro() {
        return decorado.listarDentro();
    }
}
