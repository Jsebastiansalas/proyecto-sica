package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.funcionario.CrearFuncionarioComando;
import com.acme.sica.aplicacion.funcionario.EditarFuncionarioComando;
import com.acme.sica.dominio.modelo.Funcionario;

import java.util.List;

/**
 * Puerto de entrada para la gestión de funcionarios (HU-09).
 */
public interface GestionarFuncionarioCasoUso {

    Funcionario crear(CrearFuncionarioComando comando);

    Funcionario editar(EditarFuncionarioComando comando);

    void eliminar(Long funcionarioId);

    List<Funcionario> listarTodos();

    Funcionario obtenerPorId(Long funcionarioId);
}
