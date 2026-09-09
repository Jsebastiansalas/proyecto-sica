package com.acme.sica.aplicacion.autenticacion;

import com.acme.sica.dominio.excepciones.CredencialesInvalidasExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.HasheadorContrasenas;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Servicio de aplicación que implementa el caso de uso de login.
 * Valida credenciales, crea la sesión y audita el resultado.
 */
public class IniciarSesionServicio implements IniciarSesionCasoUso {

    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final int MINUTOS_BLOQUEO = 15;

    private final UsuarioRepositorioPuerto usuarioRepositorio;
    private final HasheadorContrasenas hasheadorContrasenas;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;
    private final Map<String, IntentosLogin> intentosPorUsuario = new ConcurrentHashMap<>();

    public IniciarSesionServicio(UsuarioRepositorioPuerto usuarioRepositorio,
                                 HasheadorContrasenas hasheadorContrasenas,
                                 BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.hasheadorContrasenas = hasheadorContrasenas;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Ejecuta la operación principal del caso de uso.
     */
    @Override
    public LoginResultado ejecutar(IniciarSesionComando comando) {
        String username = comando.getUsername();
        verificarBloqueo(username);

        Optional<Usuario> usuarioOpt = usuarioRepositorio.buscarPorUsername(username);

        if (usuarioOpt.isEmpty()
                || !usuarioOpt.get().isActivo()
                || !hasheadorContrasenas.verificar(comando.getPassword(), usuarioOpt.get().getPassword())) {

            registrarIntentoFallido(username, comando.getPuntoAcceso());
            throw new CredencialesInvalidasExcepcion("Credenciales inválidas");
        }

        intentosPorUsuario.remove(username);

        Usuario usuario = usuarioOpt.get();
        Set<String> permisos = extraerPermisos(usuario);
        PuntoAcceso puntoAcceso = comando.getPuntoAcceso();
        Sesion sesion = new Sesion(usuario.getId(), usuario.getUsername(), usuario.getNombreCompleto(), permisos, puntoAcceso);
        SesionContexto.iniciar(sesion);

        registrarLoginExitoso(usuario, puntoAcceso);

        return new LoginResultado(usuario.getId(), usuario.getNombreCompleto(), usuario.getUsername(), permisos);
    }

    private Set<String> extraerPermisos(Usuario usuario) {
        return usuario.getRoles().stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .map(Permiso::getNombre)
                .collect(Collectors.toSet());
    }

    private void registrarLoginExitoso(Usuario usuario, PuntoAcceso puntoAcceso) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                usuario.getId(),
                usuario.getUsername(),
                TipoAccionAuditoria.LOGIN_EXITOSO.name(),
                "USUARIO",
                usuario.getId(),
                "Inicio de sesión exitoso",
                null,
                puntoAcceso
        );
        bitacoraRepositorio.guardar(registro);
    }

    private void registrarLoginFallido(String username, PuntoAcceso puntoAcceso) {
        BitacoraAuditoria registro = new BitacoraAuditoria(
                null,
                username,
                TipoAccionAuditoria.LOGIN_FALLIDO.name(),
                "USUARIO",
                null,
                "Intento fallido de inicio de sesión",
                null,
                puntoAcceso
        );
        bitacoraRepositorio.guardar(registro);
    }

    private void verificarBloqueo(String username) {
        IntentosLogin intentos = intentosPorUsuario.get(username);
        if (intentos == null) {
            return;
        }
        if (intentos.estaBloqueado()) {
            throw new CredencialesInvalidasExcepcion(
                    "Cuenta temporalmente bloqueada. Intente nuevamente en " + intentos.minutosRestantes() + " minutos.");
        }
        if (intentos.estaExpirado()) {
            intentosPorUsuario.remove(username);
        }
    }

    private void registrarIntentoFallido(String username, PuntoAcceso puntoAcceso) {
        IntentosLogin intentos = intentosPorUsuario.computeIfAbsent(username, k -> new IntentosLogin());
        intentos.registrarFallido();
        registrarLoginFallido(username, puntoAcceso);
    }

    private static class IntentosLogin {
        private int contador;
        private LocalDateTime ultimoIntento;
        private LocalDateTime horaBloqueo;

        void registrarFallido() {
            if (estaExpirado()) {
                contador = 0;
                horaBloqueo = null;
            }
            contador++;
            ultimoIntento = LocalDateTime.now();
            if (contador >= MAX_INTENTOS_FALLIDOS) {
                horaBloqueo = LocalDateTime.now();
            }
        }

        boolean estaBloqueado() {
            return horaBloqueo != null && LocalDateTime.now().isBefore(horaBloqueo.plusMinutes(MINUTOS_BLOQUEO));
        }

        boolean estaExpirado() {
            return ultimoIntento != null && LocalDateTime.now().isAfter(ultimoIntento.plusMinutes(MINUTOS_BLOQUEO));
        }

        long minutosRestantes() {
            if (horaBloqueo == null) {
                return 0;
            }
            long restantes = MINUTOS_BLOQUEO - java.time.Duration.between(horaBloqueo, LocalDateTime.now()).toMinutes();
            return Math.max(0, restantes);
        }
    }

}
