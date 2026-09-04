package com.acme.sica.aplicacion.visita;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para registrar el check-in de un invitado.
 */
public class CheckInInvitadoComando {

    private final String documento;

    public CheckInInvitadoComando(String documento) {
        this.documento = documento;
    }

    public String getDocumento() {
        return documento;
    }
}
