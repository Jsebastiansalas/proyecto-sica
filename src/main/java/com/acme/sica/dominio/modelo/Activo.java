package com.acme.sica.dominio.modelo;

/**
 * Entidad de dominio que representa un activo (ej. laptop, equipo) ingresado en una visita.
 */
public class Activo {

    private Long id;
    private String descripcion;
    private String numeroSerie;

    public Activo() {
    }

    public Activo(String descripcion, String numeroSerie) {
        this.descripcion = descripcion;
        this.numeroSerie = numeroSerie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    @Override
    public String toString() {
        return "Activo{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", numeroSerie='" + numeroSerie + '\'' +
                '}';
    }
}
