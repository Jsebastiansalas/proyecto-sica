package com.acme.sica.aplicacion.permiso;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PermisoRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de permisos.
 * La autorización se delega a una cadena de responsabilidad (Chain of Responsibility),
 * y la auditoría se mantiene centralizada en este servicio.
 */
public class GestionarPermisoServicio implements GestionarPermisoCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_permisos";

    private final PermisoRepositorioPuerto permisoRepositorio;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarPermisoServicio(PermisoRepositorioPuerto permisoRepositorio,
                                    BitacoraRepositorioPuerto bitacoraRepositorio,
                                    ManejadorAutorizacion cadenaAutorizacion) {
        this.permisoRepositorio = permisoRepositorio;
        this.bitacoraRepositorio = bitacoraRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Permiso crear(CrearPermisoComando comando) {
        autorizar("crear permisos");
        verificarCodigoDuplicado(comando.getNombre());

        Permiso permiso = new Permiso(comando.getNombre(), comando.getDescripcion());
        Permiso guardado = permisoRepositorio.guardar(permiso);

        auditar(TipoAccionAuditoria.CREAR_PERMISO, guardado.getId(), "Permiso creado: " + guardado.getNombre());
        return guardado;
    }

    @Override
    public Permiso editar(EditarPermisoComando comando) {
        autorizar("editar permisos");

        Permiso permiso = permisoRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + comando.getId()));

        if (!permiso.getNombre().equals(comando.getNombre())) {
            verificarCodigoDuplicado(comando.getNombre());
        }

        permiso.setNombre(comando.getNombre());
        permiso.setDescripcion(comando.getDescripcion());
        Permiso actualizado = permisoRepositorio.guardar(permiso);

        auditar(TipoAccionAuditoria.EDITAR_PERMISO, actualizado.getId(), "Permiso editado: " + actualizado.getNombre());
        return actualizado;
    }

    @Override
    public void eliminar(Long permisoId) {
        autorizar("eliminar permisos");

        Permiso permiso = permisoRepositorio.buscarPorId(permisoId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + permisoId));

        permisoRepositorio.eliminarPorId(permisoId);
        auditar(TipoAccionAuditoria.ELIMINAR_PERMISO, permisoId, "Permiso eliminado: " + permiso.getNombre());
    }

    @Override
    public List<Permiso> listarTodos() {
        autorizar("listar permisos");
        return permisoRepositorio.listarTodos();
    }

    @Override
    public Permiso obtenerPorId(Long permisoId) {
        autorizar("obtener permisos");
        return permisoRepositorio.buscarPorId(permisoId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + permisoId));
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }

    private void verificarCodigoDuplicado(String codigo) {
        if (permisoRepositorio.existePorCodigo(codigo)) {
            throw new IllegalArgumentException("Ya existe un permiso con el código '" + codigo + "'");
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

}
