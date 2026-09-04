package com.acme.sica.dominio.excepciones;

/**
 * Excepción de dominio que se lanza cuando se intenta registrar el ingreso de una persona bloqueada.
 */
public class PersonaBloqueadaExcepcion extends RuntimeException {

    public PersonaBloqueadaExcepcion(String mensaje) {
        super(mensaje);
    }

}