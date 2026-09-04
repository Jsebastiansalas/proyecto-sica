package com.acme.sica.aplicacion.persona;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para bloquear una persona.
 */
public class BloquearPersonaComando {

    private final Long personaId;
    private final String motivo;

    public BloquearPersonaComando(Long personaId, String motivo) {
        this.personaId = personaId;
        this.motivo = motivo;
    }

    public Long getPersonaId() { return personaId; }
    public String getMotivo() { return motivo; }
}
