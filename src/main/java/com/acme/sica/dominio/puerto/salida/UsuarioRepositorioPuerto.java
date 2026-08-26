package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorioPuerto {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    List<Usuario> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorUsername(String username);

    long contarUsuariosPorRol(Long rolId);
}