package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.usuario.CrearUsuarioComando;
import com.acme.sica.aplicacion.usuario.EditarUsuarioComando;
import com.acme.sica.dominio.modelo.Usuario;

import java.util.List;

/**
 * Puerto de entrada para la gestión de usuarios del sistema.
 */
public interface GestionarUsuarioCasoUso {

    Usuario crear(CrearUsuarioComando comando);

    Usuario editar(EditarUsuarioComando comando);

    void eliminar(Long usuarioId);

    List<Usuario> listarTodos();

    Usuario obtenerPorId(Long usuarioId);
}