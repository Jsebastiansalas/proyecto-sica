package com.acme.sica.dominio.modelo;

public class Empresa {

    private Long id;
    private String nombre;
    private String ubicacion;
    private boolean activa;

    public Empresa() {
        this.activa = true;
    }

    public Empresa(String nombre, String ubicacion) {
        this();
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", activa=" + activa +
                '}';
    }
}