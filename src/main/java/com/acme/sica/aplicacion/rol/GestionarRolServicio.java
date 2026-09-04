package com.acme.sica.aplicacion.rol;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.RolEnUsoExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de aplicación para la gestión de roles.
 *
 * Se encarga únicamente de la lógica de negocio y de la autorización
 * (Chain of Responsibility). La auditoría de acciones críticas se realiza
 * de forma automática mediante un decorador (HU-06), por lo que este
 * servicio ya no registra la bitácora manualmente.
 */
public class GestionarRolServicio implements GestionarRolCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_roles";

    private final RolRepositorioPuerto rolRepositorio;
    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarRolServicio(RolRepositorioPuerto rolRepositorio,
                                UsuarioRepositorioPuerto usuarioRepositorio,
                                ManejadorAutorizacion cadenaAutorizacion) {
        this.rolRepositorio = rolRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Rol crear(CrearRolComando comando) {
        autorizar("crear roles");
        verificarNombreDuplicado(comando.getNombre());

        Rol rol = new Rol(comando.getNombre(), comando.getDescripcion());
        return rolRepositorio.guardar(rol);
    }

    /**
     * Actualiza una entidad existente con los datos del comando.
     */
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
        return rolRepositorio.guardar(rol);
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
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
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Rol> listarTodos() {
        autorizar("listar roles");
        return rolRepositorio.listarTodos();
    }

    /**
     * Recupera una entidad a partir de su identificador.
     */
    @Override
    public Rol obtenerPorId(Long rolId) {
        autorizar("obtener roles");
        return rolRepositorio.buscarPorId(rolId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));
    }

    /**
     * Asigna los roles indicados al usuario.
     */
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
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }

    private void verificarNombreDuplicado(String nombre) {
        if (rolRepositorio.existePorNombre(nombre)) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre '" + nombre + "'");
        }
    }
}
