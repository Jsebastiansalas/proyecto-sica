package com.acme.sica.infraestructura.seguridad.autorizacion;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

/**
 * Segundo eslabón de la cadena: verifica que la sesión actual tenga
 * el permiso requerido. Si no lo tiene, registra el intento en la bitácora
 * y lanza PermisoDenegadoExcepcion.
 */
public class ManejadorPermiso extends ManejadorAutorizacion {

    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public ManejadorPermiso(BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Verifica que se cumplan las condiciones de autorización requeridas.
     */
    @Override
    public void verificar(String permiso, String accion) {
        if (!SesionContexto.tienePermiso(permiso)) {
            registrarAccesoDenegado(permiso, accion);
            throw new PermisoDenegadoExcepcion(
                    "No tiene permiso ('" + permiso + "') para " + accion);
        }
        verificarSiguiente(permiso, accion);
    }

    private void registrarAccesoDenegado(String permiso, String accion) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                SesionContexto.obtener().map(s -> s.getUsuarioId()).orElse(null),
                SesionContexto.obtener().map(s -> s.getUsername()).orElse("anonimo"),
                TipoAccionAuditoria.ACCESO_DENEGADO.name(),
                "AUTORIZACION",
                null,
                "Permiso denegado: '" + permiso + "' para " + accion,
                null
        );
        bitacoraRepositorio.guardar(registro);
    }

}
