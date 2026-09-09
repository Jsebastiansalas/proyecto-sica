package com.acme.sica.aplicacion.reporte;

import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;

import java.time.LocalDateTime;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para consultar el reporte de accesos.
 */
public class ConsultarReporteAccesosComando {
    private final LocalDateTime fechaDesde;
    private final LocalDateTime fechaHasta;
    private final Long empresaId;
    private final PuntoAcceso puntoAcceso;

    public ConsultarReporteAccesosComando(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long empresaId) {
        this(fechaDesde, fechaHasta, empresaId, null);
    }

    public ConsultarReporteAccesosComando(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long empresaId, PuntoAcceso puntoAcceso) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.empresaId = empresaId;
        this.puntoAcceso = puntoAcceso;
    }

    public LocalDateTime getFechaDesde() { return fechaDesde; }
    public LocalDateTime getFechaHasta() { return fechaHasta; }
    public Long getEmpresaId() { return empresaId; }
    public PuntoAcceso getPuntoAcceso() { return puntoAcceso; }
}
