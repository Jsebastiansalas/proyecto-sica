package com.acme.sica.domain.model;

import com.acme.sica.domain.model.enums.TipoPersona;
import java.time.LocalDateTime;

public class Persona {

    private Long id;
    private TipoPersona tipo;
    private String documentoIdentidad;
    private String nombreCompleto;
    private String fotoUrl;
    private boolean bloqueada;
    private String motivoBloqueo;
    private LocalDateTime fechaCreacion;

    public Persona() {
        this.fechaCreacion = LocalDateTime.now();
        this.bloqueada = false;
    }

    public Persona(TipoPersona tipo, String documentoIdentidad, String nombreCompleto, String fotoUrl) {
        this();
        this.tipo = tipo;
        this.documentoIdentidad = documentoIdentidad;
        this.nombreCompleto = nombreCompleto;
        this.fotoUrl = fotoUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPersona getTipo() {
        return tipo;
    }

    public void setTipo(TipoPersona tipo) {
        this.tipo = tipo;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
    }

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public void setMotivoBloqueo(String motivoBloqueo) {
        this.motivoBloqueo = motivoBloqueo;
    }

    public void bloquear(String motivo) {
        this.bloqueada = true;
        this.motivoBloqueo = motivo;
    }

    public void desbloquear() {
        this.bloqueada = false;
        this.motivoBloqueo = null;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}