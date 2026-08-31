package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Empresa;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepositorioPuerto {

    Empresa guardar(Empresa empresa);

    Optional<Empresa> buscarPorId(Long id);

    List<Empresa> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorNombre(String nombre);

    long contar();
}