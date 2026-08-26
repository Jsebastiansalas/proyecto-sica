package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Permiso;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermisoRepositorioPuerto {

    Permiso guardar(Permiso permiso);

    Optional<Permiso> buscarPorId(Long id);

    Optional<Permiso> buscarPorCodigo(String codigo);

    List<Permiso> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorCodigo(String codigo);

    Set<Permiso> buscarPorRol(Long rolId);

    Set<Permiso> buscarPorCodigos(Set<String> codigos);
}