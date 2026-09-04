package com.acme.sica.aplicacion.reporte;

import java.time.LocalDateTime;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para consultar el reporte de accesos.
 */
public class ConsultarReporteAccesosComando {
    private final LocalDateTime fechaDesde;
    private final LocalDateTime fechaHasta;
    private final Long empresaId;

    public ConsultarReporteAccesosComando(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long empresaId) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.empresaId = empresaId;
    }

    public LocalDateTime getFechaDesde() { return fechaDesde; }
    public LocalDateTime getFechaHasta() { return fechaHasta; }
    public Long getEmpresaId() { return empresaId; }
}
