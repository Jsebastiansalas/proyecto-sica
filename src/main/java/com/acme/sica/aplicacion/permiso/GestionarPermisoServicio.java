package com.acme.sica.aplicacion.permiso;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PermisoRepositorioPuerto;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de permisos.
 * Valida permisos, reglas de negocio y audita cada operación crítica.
 */
public class GestionarPermisoServicio implements GestionarPermisoCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_permisos";

    private final PermisoRepositorioPuerto permisoRepositorio;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public GestionarPermisoServicio(PermisoRepositorioPuerto permisoRepositorio,
                                    BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.permisoRepositorio = permisoRepositorio;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Permiso crear(CrearPermisoComando comando) {
        validarPermiso("crear");

        if (permisoRepositorio.existePorCodigo(comando.getNombre())) {
            throw new IllegalArgumentException("Ya existe un permiso con el código '" + comando.getNombre() + "'");
        }

        Permiso permiso = new Permiso(comando.getNombre(), comando.getDescripcion());
        Permiso guardado = permisoRepositorio.guardar(permiso);

        auditar(TipoAccionAuditoria.CREAR_PERMISO, guardado.getId(), "Permiso creado: " + guardado.getNombre());
        return guardado;
    }

    @Override
    public Permiso editar(EditarPermisoComando comando) {
        validarPermiso("editar");

        Permiso permiso = permisoRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + comando.getId()));

        if (!permiso.getNombre().equals(comando.getNombre()) && permisoRepositorio.existePorCodigo(comando.getNombre())) {
            throw new IllegalArgumentException("Ya existe un permiso con el código '" + comando.getNombre() + "'");
        }

        permiso.setNombre(comando.getNombre());
        permiso.setDescripcion(comando.getDescripcion());
        Permiso actualizado = permisoRepositorio.guardar(permiso);

        auditar(TipoAccionAuditoria.EDITAR_PERMISO, actualizado.getId(), "Permiso editado: " + actualizado.getNombre());
        return actualizado;
    }

    @Override
    public void eliminar(Long permisoId) {
        validarPermiso("eliminar");

        Permiso permiso = permisoRepositorio.buscarPorId(permisoId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + permisoId));

        permisoRepositorio.eliminarPorId(permisoId);
        auditar(TipoAccionAuditoria.ELIMINAR_PERMISO, permisoId, "Permiso eliminado: " + permiso.getNombre());
    }

    @Override
    public List<Permiso> listarTodos() {
        validarPermiso("listar");
        return permisoRepositorio.listarTodos();
    }

    @Override
    public Permiso obtenerPorId(Long permisoId) {
        validarPermiso("obtener");
        return permisoRepositorio.buscarPorId(permisoId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + permisoId));
    }

    private void validarPermiso(String accion) {
        if (!SesionContexto.tienePermiso(PERMISO_REQUERIDO)) {
            registrarAccesoDenegado(accion);
            throw new PermisoDenegadoExcepcion("No tiene permiso para " + accion + " permisos");
        }
    }

    private void auditar(TipoAccionAuditoria accion, Long entidadId, String detalle) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                SesionContexto.obtener().map(s -> s.getUsuarioId()).orElse(null),
                SesionContexto.obtener().map(s -> s.getUsername()).orElse("sistema"),
                accion.name(),
                "PERMISO",
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
                "PERMISO",
                null,
                "Intento de " + accion + " permiso sin permiso",
                null
        );
        bitacoraRepositorio.guardar(registro);
    }

}
