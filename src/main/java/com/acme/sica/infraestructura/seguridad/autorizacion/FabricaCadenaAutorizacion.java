package com.acme.sica.infraestructura.seguridad.autorizacion;

import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

/**
 * Construye la cadena de responsabilidad para autorización.
 *
 * Orden de la cadena:
 *   1. ManejadorSesionActiva  → valida que haya sesión
 *   2. ManejadorPermiso        → valida el permiso específico
 *
 * Cualquier eslabón puede rechazar la operación lanzando
 * PermisoDenegadoExcepcion, lo que detiene la cadena.
 */
public final class FabricaCadenaAutorizacion {

    private FabricaCadenaAutorizacion() {
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    public static ManejadorAutorizacion crear(BitacoraRepositorioPuerto bitacoraRepositorio) {
        ManejadorSesionActiva sesion = new ManejadorSesionActiva();
        ManejadorPermiso permiso = new ManejadorPermiso(bitacoraRepositorio);
        sesion.setSiguiente(permiso);
        return sesion;
    }

}
