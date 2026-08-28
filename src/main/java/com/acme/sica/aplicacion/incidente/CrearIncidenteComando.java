package com.acme.sica.aplicacion.incidente;

import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;

public class CrearIncidenteComando {

    private final Long personaId;
    private final String descripcion;
    private final GravedadIncidente gravedad;

    public CrearIncidenteComando(Long personaId, String descripcion, GravedadIncidente gravedad) {
        this.personaId = personaId;
        this.descripcion = descripcion;
        this.gravedad = gravedad;
    }

    public Long getPersonaId() { return personaId; }
    public String getDescripcion() { return descripcion; }
    public GravedadIncidente getGravedad() { return gravedad; }
}
