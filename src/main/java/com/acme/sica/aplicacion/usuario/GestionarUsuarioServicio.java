package com.acme.sica.aplicacion.usuario;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.entrada.GestionarUsuarioCasoUso;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.HasheadorContrasenas;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de aplicación para la gestión de usuarios (HU-21).
 *
 * Se encarga únicamente de la lógica de negocio y de la autorización.
 * La auditoría de acciones críticas se realiza mediante un decorador.
 */
public class GestionarUsuarioServicio implements GestionarUsuarioCasoUso {

    private static final String PERMISO_REQUERIDO = "gestionar_usuarios";

    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final RolRepositorioPuerto rolRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;
    private final HasheadorContrasenas hasheadorContrasenas;

    public GestionarUsuarioServicio(UsuarioRepositorioPuerto usuarioRepositorio,
                                     RolRepositorioPuerto rolRepositorio,
                                     ManejadorAutorizacion cadenaAutorizacion,
                                     HasheadorContrasenas hasheadorContrasenas) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.rolRepositorio = rolRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
        this.hasheadorContrasenas = hasheadorContrasenas;
    }

    @Override
    public Usuario crear(CrearUsuarioComando comando) {
        autorizar("crear usuarios");
        verificarUsernameDuplicado(comando.getUsername());

        String hash = hasheadorContrasenas.hashear(comando.getPassword());
        Usuario usuario = new Usuario(comando.getUsername(), hash, comando.getNombreCompleto());
        Usuario guardado = usuarioRepositorio.guardar(usuario);

        if (comando.getRolIds() != null && !comando.getRolIds().isEmpty()) {
            Set<Long> rolesValidos = validarRoles(comando.getRolIds());
            usuarioRepositorio.asignarRoles(guardado.getId(), rolesValidos);
        }

        return usuarioRepositorio.buscarPorId(guardado.getId()).orElse(guardado);
    }

    @Override
    public Usuario editar(EditarUsuarioComando comando) {
        autorizar("editar usuarios");

        Usuario usuario = usuarioRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Usuario no encontrado con id " + comando.getId()));

        if (!usuario.getUsername().equals(comando.getUsername())) {
            verificarUsernameDuplicado(comando.getUsername());
        }

        usuario.setUsername(comando.getUsername());
        usuario.setNombreCompleto(comando.getNombreCompleto());
        usuario.setActivo(comando.isActivo());

        if (comando.getPassword() != null && !comando.getPassword().isBlank()) {
            usuario.setPassword(hasheadorContrasenas.hashear(comando.getPassword()));
        }

        usuarioRepositorio.guardar(usuario);

        if (comando.getRolIds() != null) {
            Set<Long> rolesValidos = validarRoles(comando.getRolIds());
            usuarioRepositorio.asignarRoles(usuario.getId(), rolesValidos);
        }

        return usuarioRepositorio.buscarPorId(usuario.getId()).orElse(usuario);
    }

    @Override
    public void eliminar(Long usuarioId) {
        autorizar("eliminar usuarios");

        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Usuario no encontrado con id " + usuarioId));

        if (usuario.getUsername().equals("admin")) {
            throw new IllegalArgumentException("No se puede eliminar el usuario administrador");
        }

        usuarioRepositorio.eliminarPorId(usuarioId);
    }

    @Override
    public List<Usuario> listarTodos() {
        autorizar("listar usuarios");
        return usuarioRepositorio.listarTodos();
    }

    @Override
    public Usuario obtenerPorId(Long usuarioId) {
        autorizar("obtener usuario");
        return usuarioRepositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Usuario no encontrado con id " + usuarioId));
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }

    private void verificarUsernameDuplicado(String username) {
        if (usuarioRepositorio.existePorUsername(username)) {
            throw new IllegalArgumentException("Ya existe un usuario con el username '" + username + "'");
        }
    }

    private Set<Long> validarRoles(Set<Long> rolIds) {
        Set<Long> rolesValidos = new HashSet<>();
        for (Long rolId : rolIds) {
            Rol rol = rolRepositorio.buscarPorId(rolId)
                    .orElseThrow(() -> new EntidadNoEncontradaExcepcion("Rol no encontrado con id " + rolId));
            if (!rol.isActivo()) {
                throw new IllegalArgumentException("El rol '" + rol.getNombre() + "' no está activo");
            }
            rolesValidos.add(rolId);
        }
        return rolesValidos;
    }
}