package com.acme.sica.infraestructura.seguridad.autorizacion;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;

/**
 * Primer eslabón de la cadena: verifica que exista una sesión activa.
 * Si no la hay, rechaza inmediatamente sin consultar al siguiente manejador.
 */
public class ManejadorSesionActiva extends ManejadorAutorizacion {

    /**
     * Verifica que se cumplan las condiciones de autorización requeridas.
     */
    @Override
    public void verificar(String permiso, String accion) {
        if (!SesionContexto.haySesionActiva()) {
            throw new PermisoDenegadoExcepcion("No hay una sesión activa");
        }
        verificarSiguiente(permiso, accion);
    }

}
