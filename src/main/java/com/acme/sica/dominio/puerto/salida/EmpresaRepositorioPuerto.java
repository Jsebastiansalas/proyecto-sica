package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Empresa;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida hexagonal que define las operaciones de persistencia
 * para la entidad Empresa. Es implementado por los adaptadores de infraestructura.
 */
public interface EmpresaRepositorioPuerto {

    Empresa guardar(Empresa empresa);

    Optional<Empresa> buscarPorId(Long id);

    List<Empresa> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorNombre(String nombre);

    long contar();
}