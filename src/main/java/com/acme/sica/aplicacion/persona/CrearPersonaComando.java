package com.acme.sica.aplicacion.persona;

import com.acme.sica.dominio.modelo.enumerados.TipoPersona;

public class CrearPersonaComando {

    private final TipoPersona tipo;
    private final String documentoIdentidad;
    private final String nombreCompleto;
    private final String fotoUrl;

    public CrearPersonaComando(TipoPersona tipo, String documentoIdentidad,
                               String nombreCompleto, String fotoUrl) {
        this.tipo = tipo;
        this.documentoIdentidad = documentoIdentidad;
        this.nombreCompleto = nombreCompleto;
        this.fotoUrl = fotoUrl;
    }

    public TipoPersona getTipo() {
        return tipo;
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
}
