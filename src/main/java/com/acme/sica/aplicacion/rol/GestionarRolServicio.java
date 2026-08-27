package com.acme.sica.aplicacion.rol;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.RolEnUsoExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de aplicación para la gestión de roles.
 * La autorización se delega a una cadena de responsabilidad (Chain of Responsibility),
 * y la auditoría se mantiene centralizada en este servicio.
 */
public class GestionarRolServicio implements GestionarRolCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_roles";

    private final RolRepositorioPuerto rolRepositorio;
    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarRolServicio(RolRepositorioPuerto rolRepositorio,
                                BitacoraRepositorioPuerto bitacoraRepositorio,
                                UsuarioRepositorioPuerto usuarioRepositorio,
                                ManejadorAutorizacion cadenaAutorizacion) {
        this.rolRepositorio = rolRepositorio;
        this.bitacoraRepositorio = bitacoraRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Rol crear(CrearRolComando comando) {
        autorizar("crear roles");
        verificarNombreDuplicado(comando.getNombre());

        Rol rol = new Rol(comando.getNombre(), comando.getDescripcion());
        Rol guardado = rolRepositorio.guardar(rol);

        auditar(TipoAccionAuditoria.CREAR_ROL, guardado.getId(), "Rol creado: " + guardado.getNombre());
        return guardado;
    }

    @Override
    public Rol editar(EditarRolComando comando) {
        autorizar("editar roles");

        Rol rol = rolRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + comando.getId()));

        if (!rol.getNombre().equals(comando.getNombre())) {
            verificarNombreDuplicado(comando.getNombre());
        }

        rol.setNombre(comando.getNombre());
        rol.setDescripcion(comando.getDescripcion());
        Rol actualizado = rolRepositorio.guardar(rol);

        auditar(TipoAccionAuditoria.EDITAR_ROL, actualizado.getId(), "Rol editado: " + actualizado.getNombre());
        return actualizado;
    }

    @Override
    public void eliminar(Long rolId) {
        autorizar("eliminar roles");

        Rol rol = rolRepositorio.buscarPorId(rolId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));

        long usuariosConRol = rolRepositorio.contarUsuariosConRol(rolId);
        if (usuariosConRol > 0) {
            throw new RolEnUsoExcepcion("No se puede eliminar el rol '" + rol.getNombre()
                    + "' porque tiene " + usuariosConRol + " usuario(s) asignado(s)");
        }

        rolRepositorio.eliminarPorId(rolId);
        auditar(TipoAccionAuditoria.ELIMINAR_ROL, rolId, "Rol eliminado: " + rol.getNombre());
    }

    @Override
    public List<Rol> listarTodos() {
        autorizar("listar roles");
        return rolRepositorio.listarTodos();
    }

    @Override
    public Rol obtenerPorId(Long rolId) {
        autorizar("obtener roles");
        return rolRepositorio.buscarPorId(rolId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));
    }

    @Override
    public void asignarRoles(AsignarRolesUsuarioComando comando) {
        autorizar("asignar roles");

        if (comando.getRolIds() == null || comando.getRolIds().isEmpty()) {
            throw new IllegalArgumentException("El usuario debe conservar al menos un rol");
        }

        usuarioRepositorio.buscarPorId(comando.getUsuarioId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Usuario no encontrado con id " + comando.getUsuarioId()));

        Set<Long> rolesValidos = new HashSet<>();
        for (Long rolId : comando.getRolIds()) {
            Rol rol = rolRepositorio.buscarPorId(rolId)
                    .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));
            if (!rol.isActivo()) {
                throw new IllegalArgumentException("El rol '" + rol.getNombre() + "' no está activo");
            }
            rolesValidos.add(rolId);
        }

        usuarioRepositorio.asignarRoles(comando.getUsuarioId(), rolesValidos);
        auditar(TipoAccionAuditoria.ASIGNAR_ROL_USUARIO, comando.getUsuarioId(),
                "Roles asignados: " + rolesValidos);
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }

    private void verificarNombreDuplicado(String nombre) {
        if (rolRepositorio.existePorNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre '" + nombre + "'");
        }
    }

    private void auditar(TipoAccionAuditoria accion, Long entidadId, String detalle) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                SesionContexto.obtener().map(s -> s.getUsuarioId()).orElse(null),
                SesionContexto.obtener().map(s -> s.getUsername()).orElse("sistema"),
                accion.name(),
                "ROL",
                entidadId,
                detalle,
                null
        );
        bitacoraRepositorio.guardar(registro);
    }

}
