package com.acme.sica.aplicacion.bitacora;

import java.time.LocalDateTime;

/**
 * Comando con los filtros para consultar la bitácora de auditoría.
 * Todos los filtros son opcionales (null) → se ignora ese criterio.
 */
public class ConsultarBitacoraComando {

    private final String username;
    private final String accion;
    private final String entidad;
    private final LocalDateTime fechaDesde;
    private final LocalDateTime fechaHasta;

    public ConsultarBitacoraComando(String username, String accion, String entidad,
                                    LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        this.username = username;
        this.accion = accion;
        this.entidad = entidad;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
    }

    public String getUsername() {
        return username;
    }

    public String getAccion() {
        return accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public LocalDateTime getFechaDesde() {
        return fechaDesde;
    }

    public LocalDateTime getFechaHasta() {
        return fechaHasta;
    }
}
