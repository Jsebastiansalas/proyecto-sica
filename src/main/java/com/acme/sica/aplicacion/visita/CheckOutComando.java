package com.acme.sica.aplicacion.visita;

public class CheckOutComando {

    private final String documento;

    public CheckOutComando(String documento) {
        this.documento = documento;
    }

    public String getDocumento() { return documento; }
}
