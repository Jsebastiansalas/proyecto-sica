package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Puerto de salida hexagonal que define las operaciones de persistencia
 * para la entidad Usuario. Es implementado por los adaptadores de infraestructura.
 */
public interface UsuarioRepositorioPuerto {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    List<Usuario> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorUsername(String username);

    long contar();

    void asignarRoles(Long usuarioId, Set<Long> rolIds);

    void eliminarRoles(Long usuarioId);
}
