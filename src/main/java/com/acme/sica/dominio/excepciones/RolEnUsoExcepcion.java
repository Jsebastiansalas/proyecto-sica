package com.acme.sica.dominio.excepciones;

public class RolEnUsoExcepcion extends RuntimeException {

    public RolEnUsoExcepcion(String mensaje) {
        super(mensaje);
    }

}