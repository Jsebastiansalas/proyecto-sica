package com.acme.sica.dominio.excepciones;

public class PersonaBloqueadaExcepcion extends RuntimeException {

    public PersonaBloqueadaExcepcion(String mensaje) {
        super(mensaje);
    }

}