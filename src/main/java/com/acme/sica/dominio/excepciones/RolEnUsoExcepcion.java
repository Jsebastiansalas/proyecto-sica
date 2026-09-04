package com.acme.sica.dominio.excepciones;

/**
 * Excepción de dominio que se lanza cuando se intenta eliminar un rol asignado a usuarios.
 */
public class RolEnUsoExcepcion extends RuntimeException {

    public RolEnUsoExcepcion(String mensaje) {
        super(mensaje);
    }

}