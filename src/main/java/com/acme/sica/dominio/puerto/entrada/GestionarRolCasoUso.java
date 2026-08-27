package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.rol.CrearRolComando;
import com.acme.sica.aplicacion.rol.EditarRolComando;
import com.acme.sica.aplicacion.rol.AsignarRolesUsuarioComando;
import com.acme.sica.dominio.modelo.Rol;

import java.util.List;

/**
 * Puerto de entrada para la gestión de roles del sistema.
 */
public interface GestionarRolCasoUso {

    Rol crear(CrearRolComando comando);

    Rol editar(EditarRolComando comando);

    void eliminar(Long rolId);

    List<Rol> listarTodos();

    Rol obtenerPorId(Long rolId);

    void asignarRoles(AsignarRolesUsuarioComando comando);

}
