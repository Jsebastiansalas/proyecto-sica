package com.acme.sica.aplicacion.empresa;

public class CrearEmpresaComando {

    private final String nombre;
    private final String ubicacion;

    public CrearEmpresaComando(String nombre, String ubicacion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}
