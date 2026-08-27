package com.acme.sica.dominio.excepciones;

public class EmpresaEnUsoExcepcion extends RuntimeException {

    public EmpresaEnUsoExcepcion(String mensaje) {
        super(mensaje);
    }
}
