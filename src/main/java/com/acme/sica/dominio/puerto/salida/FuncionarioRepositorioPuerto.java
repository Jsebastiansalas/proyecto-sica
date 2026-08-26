package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioRepositorioPuerto {

    Funcionario guardar(Funcionario funcionario);

    Optional<Funcionario> buscarPorId(Long id);

    Optional<Funcionario> buscarPorUsuario(Long usuarioId);

    List<Funcionario> buscarPorEmpresa(Long empresaId);

    List<Funcionario> listarTodos();

    void eliminarPorId(Long id);
}