package com.acme.sica.dominio.excepciones;

/**
 * Excepción de dominio que se lanza cuando no se encuentra una entidad requerida.
 */
public class EntidadNoEncontradaExcepcion extends RuntimeException {

    public EntidadNoEncontradaExcepcion(String mensaje) {
        super(mensaje);
    }

}