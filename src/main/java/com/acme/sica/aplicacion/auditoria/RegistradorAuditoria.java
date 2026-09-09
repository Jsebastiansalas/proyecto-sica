package com.acme.sica.aplicacion.auditoria;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

/**
 * Utilidad de aplicación para registrar entradas en la bitácora de auditoría.
 * Centraliza la construcción del registro a partir de la sesión activa,
 * evitando duplicación entre los decoradores de auditoría.
 */
public final class RegistradorAuditoria {

    private RegistradorAuditoria() {
    }

    public static void registrar(BitacoraRepositorioPuerto bitacoraRepositorio,
                                 TipoAccionAuditoria accion,
                                 String entidad,
                                 Long entidadId,
                                 String detalle) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                SesionContexto.obtener().map(s -> s.getUsuarioId()).orElse(null),
                SesionContexto.obtener().map(s -> s.getUsername()).orElse("sistema"),
                accion.name(),
                entidad,
                entidadId,
                detalle,
                null,
                SesionContexto.obtener().map(s -> s.getPuntoAcceso()).orElse(PuntoAcceso.SISTEMA)
        );
        bitacoraRepositorio.guardar(registro);
    }
}
