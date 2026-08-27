package com.acme.sica.aplicacion.rol;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.excepciones.RolEnUsoExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de roles.
 * Valida permisos, reglas de negocio y audita cada operación crítica.
 */
public class GestionarRolServicio implements GestionarRolCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_roles";

    private final RolRepositorioPuerto rolRepositorio;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public GestionarRolServicio(RolRepositorioPuerto rolRepositorio,
                                BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.rolRepositorio = rolRepositorio;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Rol crear(CrearRolComando comando) {
        validarPermiso("crear");

        if (rolRepositorio.existePorNombre(comando.getNombre())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre '" + comando.getNombre() + "'");
        }

        Rol rol = new Rol(comando.getNombre(), comando.getDescripcion());
        Rol guardado = rolRepositorio.guardar(rol);

        auditar(TipoAccionAuditoria.CREAR_ROL, guardado.getId(), "Rol creado: " + guardado.getNombre());
        return guardado;
    }

    @Override
    public Rol editar(EditarRolComando comando) {
        validarPermiso("editar");

        Rol rol = rolRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + comando.getId()));

        if (!rol.getNombre().equals(comando.getNombre()) && rolRepositorio.existePorNombre(comando.getNombre())) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre '" + comando.getNombre() + "'");
        }

        rol.setNombre(comando.getNombre());
        rol.setDescripcion(comando.getDescripcion());
        Rol actualizado = rolRepositorio.guardar(rol);

        auditar(TipoAccionAuditoria.EDITAR_ROL, actualizado.getId(), "Rol editado: " + actualizado.getNombre());
        return actualizado;
    }

    @Override
    public void eliminar(Long rolId) {
        validarPermiso("eliminar");

        Rol rol = rolRepositorio.buscarPorId(rolId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));

        long usuariosConRol = rolRepositorio.contarUsuariosConRol(rolId);
        if (usuariosConRol > 0) {
            throw new RolEnUsoExcepcion("No se puede eliminar el rol '" + rol.getNombre() + "' porque tiene " + usuariosConRol + " usuario(s) asignado(s)");
        }

        rolRepositorio.eliminarPorId(rolId);
        auditar(TipoAccionAuditoria.ELIMINAR_ROL, rolId, "Rol eliminado: " + rol.getNombre());
    }

    @Override
    public List<Rol> listarTodos() {
        validarPermiso("listar");
        return rolRepositorio.listarTodos();
    }

    @Override
    public Rol obtenerPorId(Long rolId) {
        validarPermiso("obtener");
        return rolRepositorio.buscarPorId(rolId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));
    }

    private void validarPermiso(String accion) {
        if (!SesionContexto.tienePermiso(PERMISO_REQUERIDO)) {
            registrarAccesoDenegado(accion);
            throw new PermisoDenegadoExcepcion("No tiene permiso para " + accion + " roles");
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

    private void registrarAccesoDenegado(String accion) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                SesionContexto.obtener().map(s -> s.getUsuarioId()).orElse(null),
                SesionContexto.obtener().map(s -> s.getUsername()).orElse("anonimo"),
                TipoAccionAuditoria.ACCESO_DENEGADO.name(),
                "ROL",
                null,
                "Intento de " + accion + " rol sin permiso",
                null
        );
        bitacoraRepositorio.guardar(registro);
    }

}
