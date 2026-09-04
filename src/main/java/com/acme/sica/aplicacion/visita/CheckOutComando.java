package com.acme.sica.aplicacion.visita;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para registrar el check-out de una visita.
 */
public class CheckOutComando {

    private final String documento;

    public CheckOutComando(String documento) {
        this.documento = documento;
    }

    public String getDocumento() { return documento; }
}
