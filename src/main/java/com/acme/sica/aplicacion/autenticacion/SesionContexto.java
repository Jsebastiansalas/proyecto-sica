package com.acme.sica.aplicacion.autenticacion;

import java.util.Optional;

/**
 * Mantiene la sesión actual del usuario en memoria de forma estática.
 * Es un objeto de aplicación, no del dominio, porque representa estado temporal de la interfaz.
 */
public class SesionContexto {

    private static Sesion sesionActual;

    private SesionContexto() {
        // Utilidad, no se instancia
    }

    public static void iniciar(Sesion sesion) {
        sesionActual = sesion;
    }

    public static void cerrar() {
        sesionActual = null;
    }

    public static Optional<Sesion> obtener() {
        if (sesionActual != null && sesionActual.estaExpirada()) {
            cerrar();
        }
        return Optional.ofNullable(sesionActual);
    }

    public static boolean haySesionActiva() {
        if (sesionActual != null && sesionActual.estaExpirada()) {
            cerrar();
            return false;
        }
        return sesionActual != null;
    }

    public static boolean tienePermiso(String permiso) {
        if (sesionActual != null && sesionActual.estaExpirada()) {
            cerrar();
            return false;
        }
        return sesionActual != null && sesionActual.tienePermiso(permiso);
    }

}
