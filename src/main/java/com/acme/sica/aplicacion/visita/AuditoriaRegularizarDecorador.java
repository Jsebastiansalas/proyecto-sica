package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.RegularizarSalidaCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para regularización de salidas olvidadas (HU-06).
 * Registra SALIDA_OLVIDADA o NUEVO_INGRESO según el tipo de regularización.
 */
public class AuditoriaRegularizarDecorador implements RegularizarSalidaCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final RegularizarSalidaCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaRegularizarDecorador(RegularizarSalidaCasoUso decorado,
                                         BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Regulariza la salida de una visita según el tipo indicado.
     */
    @Override
    public Visita regularizar(RegularizarSalidaComando comando) {
        Visita visita = decorado.regularizar(comando);
        TipoAccionAuditoria accion = comando.getTipo() == TipoRegularizacion.NUEVO_INGRESO
                ? TipoAccionAuditoria.NUEVO_INGRESO
                : TipoAccionAuditoria.SALIDA_OLVIDADA;
        RegistradorAuditoria.registrar(bitacoraRepositorio, accion,
                ENTIDAD, visita.getId(),
                "Salida olvidada regularizada: " + visita.getPersona().getNombreCompleto()
                        + " (" + comando.getTipo() + ")");
        return visita;
    }

    @Override
    public List<Visita> listarSalidasOlvidadas() {
        return decorado.listarSalidasOlvidadas();
    }
}
