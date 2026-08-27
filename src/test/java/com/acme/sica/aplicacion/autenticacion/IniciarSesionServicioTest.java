package com.acme.sica.aplicacion.autenticacion;

import com.acme.sica.dominio.excepciones.CredencialesInvalidasExcepcion;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.HasheadorContrasenas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IniciarSesionServicioTest {

    @Mock
    private UsuarioRepositorioPuerto usuarioRepositorio;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private HasheadorContrasenas hasheadorContrasenas;
    private IniciarSesionServicio servicio;

    @BeforeEach
    void setUp() {
        hasheadorContrasenas = new HasheadorContrasenas();
        servicio = new IniciarSesionServicio(usuarioRepositorio, hasheadorContrasenas, bitacoraRepositorio);
    }

    @Test
    void loginExitoso_conCredencialesValidas() {
        // Given
        String username = "admin";
        String password = "admin123";
        String hashAlmacenado = hasheadorContrasenas.hashear(password);

        Set<Rol> roles = new HashSet<>();
        Rol rolAdmin = new Rol("ADMINISTRADOR", "Administrador del sistema");
        rolAdmin.setId(1L);
        Set<Permiso> permisosAdmin = new HashSet<>();
        Permiso permisoLogin = new Permiso("login", "Iniciar sesión");
        permisoLogin.setId(1L);
        Permiso permisoGestionarRoles = new Permiso("gestionar_roles", "Gestionar roles");
        permisoGestionarRoles.setId(2L);
        permisosAdmin.add(permisoLogin);
        permisosAdmin.add(permisoGestionarRoles);
        rolAdmin.setPermisos(permisosAdmin);
        roles.add(rolAdmin);

        Usuario usuario = new Usuario(username, hashAlmacenado, "Administrador Principal");
        usuario.setId(1L);
        usuario.setActivo(true);
        usuario.setRoles(roles);

        when(usuarioRepositorio.buscarPorUsername(username)).thenReturn(Optional.of(usuario));

        // When
        IniciarSesionComando comando = new IniciarSesionComando(username, password);
        LoginResultado resultado = servicio.ejecutar(comando);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals("Administrador Principal", resultado.getNombreCompleto());
        assertEquals(username, resultado.getUsername());
        assertTrue(resultado.getPermisos().contains("login"));
        assertTrue(resultado.getPermisos().contains("gestionar_roles"));

        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void loginFallido_conCredencialesInvalidas() {
        // Given
        String username = "admin";
        String password = "wrongpassword";
        String hashAlmacenado = hasheadorContrasenas.hashear("admin123");

        Usuario usuario = new Usuario(username, hashAlmacenado, "Administrador Principal");
        usuario.setId(1L);
        usuario.setActivo(true);

        when(usuarioRepositorio.buscarPorUsername(username)).thenReturn(Optional.of(usuario));

        // When & Then
        IniciarSesionComando comando = new IniciarSesionComando(username, password);
        assertThrows(CredencialesInvalidasExcepcion.class, () -> servicio.ejecutar(comando));

        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void loginFallido_usuarioNoExiste() {
        // Given
        String username = "nonexistent";
        String password = "password";

        when(usuarioRepositorio.buscarPorUsername(username)).thenReturn(Optional.empty());

        // When & Then
        IniciarSesionComando comando = new IniciarSesionComando(username, password);
        assertThrows(CredencialesInvalidasExcepcion.class, () -> servicio.ejecutar(comando));

        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void loginFallido_usuarioInactivo() {
        // Given
        String username = "admin";
        String password = "admin123";
        String hashAlmacenado = hasheadorContrasenas.hashear(password);

        Usuario usuario = new Usuario(username, hashAlmacenado, "Administrador Principal");
        usuario.setId(1L);
        usuario.setActivo(false);

        when(usuarioRepositorio.buscarPorUsername(username)).thenReturn(Optional.of(usuario));

        // When & Then
        IniciarSesionComando comando = new IniciarSesionComando(username, password);
        assertThrows(CredencialesInvalidasExcepcion.class, () -> servicio.ejecutar(comando));

        verify(bitacoraRepositorio).guardar(any());
    }

}
