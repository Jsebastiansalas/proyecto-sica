package com.acme.sica.aplicacion.permiso;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.dominio.puerto.salida.PermisoRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de permisos.
 *
 * Se encarga únicamente de la lógica de negocio y de la autorización
 * (Chain of Responsibility). La auditoría de acciones críticas se realiza
 * de forma automática mediante un decorador (HU-06), por lo que este
 * servicio ya no registra la bitácora manualmente.
 */
public class GestionarPermisoServicio implements GestionarPermisoCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_permisos";

    private final PermisoRepositorioPuerto permisoRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarPermisoServicio(PermisoRepositorioPuerto permisoRepositorio,
                                    ManejadorAutorizacion cadenaAutorizacion) {
        this.permisoRepositorio = permisoRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Permiso crear(CrearPermisoComando comando) {
        autorizar("crear permisos");
        verificarCodigoDuplicado(comando.getNombre());

        Permiso permiso = new Permiso(comando.getNombre(), comando.getDescripcion());
        return permisoRepositorio.guardar(permiso);
    }

    /**
     * Actualiza una entidad existente con los datos del comando.
     */
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
        return permisoRepositorio.guardar(permiso);
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminar(Long permisoId) {
        autorizar("eliminar permisos");

        Permiso permiso = permisoRepositorio.buscarPorId(permisoId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Permiso no encontrado con id " + permisoId));

        permisoRepositorio.eliminarPorId(permisoId);
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Permiso> listarTodos() {
        autorizar("listar permisos");
        return permisoRepositorio.listarTodos();
    }

    /**
     * Recupera una entidad a partir de su identificador.
     */
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
}
