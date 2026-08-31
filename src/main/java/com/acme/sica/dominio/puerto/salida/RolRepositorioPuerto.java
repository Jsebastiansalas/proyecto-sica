package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Rol;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RolRepositorioPuerto {

    Rol guardar(Rol rol);

    Optional<Rol> buscarPorId(Long id);

    Optional<Rol> buscarPorNombre(String nombre);

    List<Rol> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorNombre(String nombre);

    long contar();

    long contarUsuariosConRol(Long rolId);

    Set<Long> buscarIdsPermisosPorRol(Long rolId);

    void asignarPermisos(Long rolId, Set<Long> permisoIds);

    void eliminarPermisos(Long rolId);
}