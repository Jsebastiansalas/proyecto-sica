package com.acme.sica.dominio.modelo;

import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;

import java.time.LocalDateTime;

public class Incidente {

    private Long id;
    private Persona persona;
    private Empresa empresa;
    private String descripcion;
    private GravedadIncidente gravedad;
    private Usuario registradoPor;
    private LocalDateTime fechaCreacion;

    public Incidente() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Incidente(Persona persona, Empresa empresa, String descripcion,
                     GravedadIncidente gravedad, Usuario registradoPor) {
        this();
        this.persona = persona;
        this.empresa = empresa;
        this.descripcion = descripcion;
        this.gravedad = gravedad;
        this.registradoPor = registradoPor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public GravedadIncidente getGravedad() {
        return gravedad;
    }

    public void setGravedad(GravedadIncidente gravedad) {
        this.gravedad = gravedad;
    }

    public Usuario getRegistradoPor() {
        return registradoPor;
    }

    public void setRegistradoPor(Usuario registradoPor) {
        this.registradoPor = registradoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return "Incidente{" +
                "id=" + id +
                ", persona=" + (persona != null ? persona.getNombreCompleto() : null) +
                ", empresa=" + (empresa != null ? empresa.getNombre() : null) +
                ", gravedad=" + gravedad +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}