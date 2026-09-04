package com.acme.sica.aplicacion.visita;

import java.time.LocalDateTime;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para pre-registrar un invitado.
 */
public class PreRegistrarInvitadoComando {

    private final Long personaId;
    private final Long funcionarioId;
    private final LocalDateTime fechaHoraEsperada;
    private final String motivo;

    public PreRegistrarInvitadoComando(Long personaId, Long funcionarioId,
                                       LocalDateTime fechaHoraEsperada, String motivo) {
        this.personaId = personaId;
        this.funcionarioId = funcionarioId;
        this.fechaHoraEsperada = fechaHoraEsperada;
        this.motivo = motivo;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public LocalDateTime getFechaHoraEsperada() {
        return fechaHoraEsperada;
    }

    public String getMotivo() {
        return motivo;
    }
}
