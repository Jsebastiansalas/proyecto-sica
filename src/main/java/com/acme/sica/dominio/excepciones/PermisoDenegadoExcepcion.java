package com.acme.sica.dominio.excepciones;

public class PermisoDenegadoExcepcion extends RuntimeException {

    public PermisoDenegadoExcepcion(String mensaje) {
        super(mensaje);
    }

}