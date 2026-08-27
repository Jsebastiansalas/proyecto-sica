package com.acme.sica.aplicacion.rol;

/**
 * Comando para editar un rol existente.
 */
public class EditarRolComando {

    private final Long id;
    private final String nombre;
    private final String descripcion;

    public EditarRolComando(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
