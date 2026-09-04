package com.acme.sica.aplicacion.rol;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para la gestión de roles (HU-06).
 *
 * Aplica el patrón Decorator: implementa el mismo puerto de entrada
 * {@link GestionarRolCasoUso} y envuelve al servicio real, registrando
 * automáticamente en la bitácora cada acción crítica (crear, editar,
 * eliminar y asignar roles) sin que el servicio se preocupe por auditar.
 *
 * Las operaciones de lectura (listar/obtener) no se auditan.
 */
public class AuditoriaRolDecorador implements GestionarRolCasoUso {

    private static final String ENTIDAD = "ROL";

    private final GestionarRolCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaRolDecorador(GestionarRolCasoUso decorado,
                                 BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Rol crear(CrearRolComando comando) {
        Rol rol = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_ROL,
                ENTIDAD, rol.getId(), "Rol creado: " + rol.getNombre());
        return rol;
    }

    /**
     * Actualiza una entidad existente con los datos del comando.
     */
    @Override
    public Rol editar(EditarRolComando comando) {
        Rol rol = decorado.editar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.EDITAR_ROL,
                ENTIDAD, rol.getId(), "Rol editado: " + rol.getNombre());
        return rol;
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminar(Long rolId) {
        decorado.eliminar(rolId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ELIMINAR_ROL,
                ENTIDAD, rolId, "Rol eliminado: id=" + rolId);
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Rol> listarTodos() {
        return decorado.listarTodos();
    }

    /**
     * Recupera una entidad a partir de su identificador.
     */
    @Override
    public Rol obtenerPorId(Long rolId) {
        return decorado.obtenerPorId(rolId);
    }

    /**
     * Asigna los roles indicados al usuario.
     */
    @Override
    public void asignarRoles(AsignarRolesUsuarioComando comando) {
        decorado.asignarRoles(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ASIGNAR_ROL_USUARIO,
                "USUARIO", comando.getUsuarioId(), "Roles asignados: " + comando.getRolIds());
    }
}
