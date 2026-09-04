package com.acme.sica.aplicacion.visita;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para registrar una visita no anunciada.
 */
public class RegistrarNoAnunciadoComando {

    private final String documento;
    private final String nombreCompleto;
    private final String fotoUrl;
    private final Long funcionarioId;
    private final String motivo;

    public RegistrarNoAnunciadoComando(String documento, String nombreCompleto, String fotoUrl,
                                       Long funcionarioId, String motivo) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.fotoUrl = fotoUrl;
        this.funcionarioId = funcionarioId;
        this.motivo = motivo;
    }

    public String getDocumento() { return documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getFotoUrl() { return fotoUrl; }
    public Long getFuncionarioId() { return funcionarioId; }
    public String getMotivo() { return motivo; }
}
