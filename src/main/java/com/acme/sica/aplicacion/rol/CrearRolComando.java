package com.acme.sica.aplicacion.rol;

/**
 * Comando para crear un nuevo rol.
 */
public class CrearRolComando {

    private final String nombre;
    private final String descripcion;

    public CrearRolComando(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
