package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.empresa.CrearEmpresaComando;
import com.acme.sica.aplicacion.empresa.EditarEmpresaComando;
import com.acme.sica.dominio.modelo.Empresa;

import java.util.List;

/**
 * Puerto de entrada para la gestión de empresas (HU-08).
 */
public interface GestionarEmpresaCasoUso {

    Empresa crear(CrearEmpresaComando comando);

    Empresa editar(EditarEmpresaComando comando);

    void eliminar(Long empresaId);

    List<Empresa> listarTodos();

    Empresa obtenerPorId(Long empresaId);
}
