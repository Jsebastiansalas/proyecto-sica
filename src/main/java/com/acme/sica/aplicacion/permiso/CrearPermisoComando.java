package com.acme.sica.aplicacion.permiso;

/**
 * Comando para crear un nuevo permiso.
 */
public class CrearPermisoComando {

    private final String nombre;
    private final String descripcion;

    public CrearPermisoComando(String nombre, String descripcion) {
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
