package com.acme.sica.aplicacion.usuario;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarUsuarioCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para GestionarUsuarioServicio (HU-06).
 */
public class AuditoriaUsuarioDecorador implements GestionarUsuarioCasoUso {

    private static final String ENTIDAD = "USUARIO";

    private final GestionarUsuarioCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaUsuarioDecorador(GestionarUsuarioCasoUso decorado,
                                      BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Usuario crear(CrearUsuarioComando comando) {
        Usuario usuario = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_USUARIO,
                ENTIDAD, usuario.getId(), "Usuario creado: " + usuario.getUsername());
        return usuario;
    }

    @Override
    public Usuario editar(EditarUsuarioComando comando) {
        Usuario usuario = decorado.editar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.EDITAR_USUARIO,
                ENTIDAD, usuario.getId(), "Usuario editado: " + usuario.getUsername());
        return usuario;
    }

    @Override
    public void eliminar(Long usuarioId) {
        decorado.eliminar(usuarioId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.ELIMINAR_USUARIO,
                ENTIDAD, usuarioId, "Usuario eliminado: id=" + usuarioId);
    }

    @Override
    public List<Usuario> listarTodos() {
        return decorado.listarTodos();
    }

    @Override
    public Usuario obtenerPorId(Long usuarioId) {
        return decorado.obtenerPorId(usuarioId);
    }
}