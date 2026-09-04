package com.acme.sica.aplicacion.persona;

import com.acme.sica.dominio.modelo.enumerados.TipoPersona;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para editar una persona.
 */
public class EditarPersonaComando {

    private final Long id;
    private final String documentoIdentidad;
    private final String nombreCompleto;
    private final String fotoUrl;
    private final TipoPersona tipo;
    private final boolean bloqueada;

    public EditarPersonaComando(Long id, String documentoIdentidad, String nombreCompleto,
                                String fotoUrl, TipoPersona tipo, boolean bloqueada) {
        this.id = id;
        this.documentoIdentidad = documentoIdentidad;
        this.nombreCompleto = nombreCompleto;
        this.fotoUrl = fotoUrl;
        this.tipo = tipo;
        this.bloqueada = bloqueada;
    }

    public Long getId() {
        return id;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public TipoPersona getTipo() {
        return tipo;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }
}
