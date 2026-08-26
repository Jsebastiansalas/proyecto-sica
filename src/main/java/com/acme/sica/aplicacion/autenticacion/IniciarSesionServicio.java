package com.acme.sica.aplicacion.autenticacion;

import com.acme.sica.dominio.excepciones.CredencialesInvalidasExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.HasheadorContrasenas;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa el caso de uso de login.
 * Valida credenciales, crea la sesión y audita el resultado.
 */
public class IniciarSesionServicio implements IniciarSesionCasoUso {

    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final HasheadorContrasenas hasheadorContrasenas;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public IniciarSesionServicio(UsuarioRepositorioPuerto usuarioRepositorio,
                                 HasheadorContrasenas hasheadorContrasenas,
                                 BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.hasheadorContrasenas = hasheadorContrasenas;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public LoginResultado ejecutar(IniciarSesionComando comando) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.buscarPorUsername(comando.getUsername());

        if (usuarioOpt.isEmpty()
                || !usuarioOpt.get().isActivo()
                || !hasheadorContrasenas.verificar(comando.getPassword(), usuarioOpt.get().getPassword())) {

            registrarLoginFallido(comando.getUsername());
            throw new CredencialesInvalidasExcepcion("Credenciales inválidas");
        }

        Usuario usuario = usuarioOpt.get();
        Set<String> permisos = extraerPermisos(usuario);
        Sesion sesion = new Sesion(usuario.getId(), usuario.getUsername(), usuario.getNombreCompleto(), permisos);
        SesionContexto.iniciar(sesion);

        registrarLoginExitoso(usuario);

        return new LoginResultado(usuario.getId(), usuario.getNombreCompleto(), usuario.getUsername(), permisos);
    }

    private Set<String> extraerPermisos(Usuario usuario) {
        return usuario.getRoles().stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .map(Permiso::getNombre)
                .collect(Collectors.toSet());
    }

    private void registrarLoginExitoso(Usuario usuario) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                usuario.getId(),
                usuario.getUsername(),
                TipoAccionAuditoria.LOGIN_EXITOSO.name(),
                "USUARIO",
                usuario.getId(),
                "Inicio de sesión exitoso",
                null
        );
        bitacoraRepositorio.guardar(registro);
    }

    private void registrarLoginFallido(String username) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                null,
                username,
                TipoAccionAuditoria.LOGIN_FALLIDO.name(),
                "USUARIO",
                null,
                "Intento fallido de inicio de sesión",
                null
        );
        bitacoraRepositorio.guardar(registro);
    }

}
