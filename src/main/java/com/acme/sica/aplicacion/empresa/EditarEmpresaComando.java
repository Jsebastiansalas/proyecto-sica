package com.acme.sica.aplicacion.empresa;

public class EditarEmpresaComando {

    private final Long id;
    private final String nombre;
    private final String ubicacion;
    private final boolean activa;

    public EditarEmpresaComando(Long id, String nombre, String ubicacion, boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public boolean isActiva() {
        return activa;
    }
}
