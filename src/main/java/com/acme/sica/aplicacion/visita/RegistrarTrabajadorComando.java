package com.acme.sica.aplicacion.visita;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para registrar el ingreso de un trabajador.
 */
public class RegistrarTrabajadorComando {

    private final String documento;
    private final String nombreCompleto;
    private final String fotoUrl;
    private final Long funcionarioId;

    public RegistrarTrabajadorComando(String documento, String nombreCompleto, String fotoUrl, Long funcionarioId) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.fotoUrl = fotoUrl;
        this.funcionarioId = funcionarioId;
    }

    public String getDocumento() { return documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getFotoUrl() { return fotoUrl; }
    public Long getFuncionarioId() { return funcionarioId; }
}
