package com.acme.sica.aplicacion.permiso;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para la gestión de permisos (HU-06).
 *
 * Aplica el patrón Decorator: implementa el mismo puerto de entrada
 * {@link GestionarPermisoCasoUso} y envuelve al servicio real, registrando
 * automáticamente en la bitácora cada acción crítica (crear, editar y
 * eliminar permisos).
 *
 * Las operaciones de lectura (listar/obtener) no se auditan.
 */
public class AuditoriaPermisoDecorador implements GestionarPermisoCasoUso {

    private static final String ENTIDAD = "PERMISO";

    private final GestionarPermisoCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaPermisoDecorador(GestionarPermisoCasoUso decorado,
                                     BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Permiso crear(CrearPermisoComando comando) {
        Permiso permiso = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_PERMISO,
                ENTIDAD, permiso.getId(), "Permiso creado: " + permiso.getNombre());
        return permiso;
    }

    @Override
    public Permiso editar(EditarPermisoComando comando) {
        Permiso permiso = decorado.editar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.EDITAR_PERMISO,
                ENTIDAD, permiso.getId(), "Permiso editado: " + permiso.getNombre());
        return permiso;
    }

    @Override
    public void eliminar(Long permisoId) {
        decorado.eliminar(permisoId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ELIMINAR_PERMISO,
                ENTIDAD, permisoId, "Permiso eliminado: id=" + permisoId);
    }

    @Override
    public List<Permiso> listarTodos() {
        return decorado.listarTodos();
    }

    @Override
    public Permiso obtenerPorId(Long permisoId) {
        return decorado.obtenerPorId(permisoId);
    }
}
