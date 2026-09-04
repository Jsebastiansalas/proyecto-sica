package com.acme.sica.dominio.excepciones;

/**
 * Excepción de dominio que se lanza cuando el usuario no tiene permisos para realizar una acción.
 */
public class PermisoDenegadoExcepcion extends RuntimeException {

    public PermisoDenegadoExcepcion(String mensaje) {
        super(mensaje);
    }

}