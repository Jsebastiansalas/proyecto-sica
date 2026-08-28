package com.acme.sica.aplicacion.reporte;

import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;
import java.time.LocalDateTime;

public class ConsultarReporteIncidentesComando {
    private final LocalDateTime fechaDesde;
    private final LocalDateTime fechaHasta;
    private final Long empresaId;
    private final GravedadIncidente gravedad;

    public ConsultarReporteIncidentesComando(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                             Long empresaId, GravedadIncidente gravedad) {
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.empresaId = empresaId;
        this.gravedad = gravedad;
    }

    public LocalDateTime getFechaDesde() { return fechaDesde; }
    public LocalDateTime getFechaHasta() { return fechaHasta; }
    public Long getEmpresaId() { return empresaId; }
    public GravedadIncidente getGravedad() { return gravedad; }
}
