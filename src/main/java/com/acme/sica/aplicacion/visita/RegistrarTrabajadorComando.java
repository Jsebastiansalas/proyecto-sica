package com.acme.sica.aplicacion.visita;

public class RegistrarTrabajadorComando {

    private final String documento;
    private final String nombreCompleto;
    private final String fotoUrl;

    public RegistrarTrabajadorComando(String documento, String nombreCompleto, String fotoUrl) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.fotoUrl = fotoUrl;
    }

    public String getDocumento() { return documento; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getFotoUrl() { return fotoUrl; }
}
