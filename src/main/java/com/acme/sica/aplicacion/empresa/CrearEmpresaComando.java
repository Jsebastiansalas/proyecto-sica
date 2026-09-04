package com.acme.sica.aplicacion.empresa;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para crear una empresa.
 */
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
