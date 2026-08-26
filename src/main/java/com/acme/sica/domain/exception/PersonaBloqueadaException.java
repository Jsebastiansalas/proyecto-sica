package com.acme.sica.domain.exception;

public class PersonaBloqueadaException extends RuntimeException {

    public PersonaBloqueadaException(String mensaje) {
        super(mensaje);
    }

}