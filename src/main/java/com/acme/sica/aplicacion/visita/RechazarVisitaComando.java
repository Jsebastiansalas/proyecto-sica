package com.acme.sica.aplicacion.visita;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para rechazar una visita.
 */
public class RechazarVisitaComando {

    private final Long visitaId;
    private final String motivo;

    public RechazarVisitaComando(Long visitaId, String motivo) {
        this.visitaId = visitaId;
        this.motivo = motivo;
    }

    public Long getVisitaId() { return visitaId; }
    public String getMotivo() { return motivo; }
}
