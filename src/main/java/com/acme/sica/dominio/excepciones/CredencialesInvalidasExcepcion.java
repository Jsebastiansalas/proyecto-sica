package com.acme.sica.dominio.excepciones;

/**
 * Excepción de dominio que se lanza cuando las credenciales proporcionadas no son válidas.
 */
public class CredencialesInvalidasExcepcion extends RuntimeException {

    public CredencialesInvalidasExcepcion(String mensaje) {
        super(mensaje);
    }

}