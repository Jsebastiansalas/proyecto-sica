package com.acme.sica.dominio.excepciones;

/**
 * Excepción de dominio que se lanza cuando se intenta eliminar una empresa que tiene funcionarios asociados.
 */
public class EmpresaEnUsoExcepcion extends RuntimeException {

    public EmpresaEnUsoExcepcion(String mensaje) {
        super(mensaje);
    }
}
