package com.acme.sica.domain.port.out;

import com.acme.sica.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorioPuerto {

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findAll();

    Usuario save(Usuario usuario);

    void deleteById(Long id);

    boolean existsByUsername(String username);

    long countByRolId(Long rolId);
}