package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.permiso.CrearPermisoComando;
import com.acme.sica.aplicacion.permiso.EditarPermisoComando;
import com.acme.sica.dominio.modelo.Permiso;

import java.util.List;

/**
 * Puerto de entrada para la gestión de permisos del sistema.
 */
public interface GestionarPermisoCasoUso {

    Permiso crear(CrearPermisoComando comando);

    Permiso editar(EditarPermisoComando comando);

    void eliminar(Long permisoId);

    List<Permiso> listarTodos();

    Permiso obtenerPorId(Long permisoId);

}
