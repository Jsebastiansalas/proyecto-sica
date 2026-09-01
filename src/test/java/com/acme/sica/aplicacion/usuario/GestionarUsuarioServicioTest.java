package com.acme.sica.aplicacion.usuario;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.HasheadorContrasenas;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarUsuarioServicioTest {

    @Mock
    private UsuarioRepositorioPuerto usuarioRepositorio;

    @Mock
    private RolRepositorioPuerto rolRepositorio;

    private HasheadorContrasenas hasheadorContrasenas;

    private ManejadorAutorizacion cadenaAutorizacion;
    private GestionarUsuarioServicio servicio;

    @BeforeEach
    void setUp() {
        hasheadorContrasenas = new HasheadorContrasenas();
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new GestionarUsuarioServicio(usuarioRepositorio, rolRepositorio, cadenaAutorizacion, hasheadorContrasenas);
    }

    @Test
    void crear_usernameUnico_creaUsuario() {
        CrearUsuarioComando comando = new CrearUsuarioComando("nuevo", "pass", "Nuevo Usuario", Set.of());
        when(usuarioRepositorio.existePorUsername("nuevo")).thenReturn(false);

        Usuario guardado = new Usuario("nuevo", "hash", "Nuevo Usuario");
        guardado.setId(1L);
        when(usuarioRepositorio.guardar(any(Usuario.class))).thenReturn(guardado);
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(guardado));

        Usuario resultado = servicio.crear(comando);

        assertEquals("nuevo", resultado.getUsername());
        assertEquals("Nuevo Usuario", resultado.getNombreCompleto());
    }

    @Test
    void crear_usernameDuplicado_lanzaExcepcion() {
        CrearUsuarioComando comando = new CrearUsuarioComando("duplicado", "pass", "Nombre", Set.of());
        when(usuarioRepositorio.existePorUsername("duplicado")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
    }

    @Test
    void crear_conRoles_asignaRolesValidos() {
        CrearUsuarioComando comando = new CrearUsuarioComando("nuevo", "pass", "Nuevo", Set.of(1L, 2L));
        when(usuarioRepositorio.existePorUsername("nuevo")).thenReturn(false);

        Usuario guardado = new Usuario("nuevo", "hash", "Nuevo");
        guardado.setId(1L);
        when(usuarioRepositorio.guardar(any(Usuario.class))).thenReturn(guardado);
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(guardado));

        Rol rol1 = new Rol("R1", "D1"); rol1.setId(1L); rol1.setActivo(true);
        Rol rol2 = new Rol("R2", "D2"); rol2.setId(2L); rol2.setActivo(true);
        when(rolRepositorio.buscarPorId(1L)).thenReturn(Optional.of(rol1));
        when(rolRepositorio.buscarPorId(2L)).thenReturn(Optional.of(rol2));

        servicio.crear(comando);

        verify(usuarioRepositorio).asignarRoles(eq(1L), eq(Set.of(1L, 2L)));
    }

    @Test
    void editar_cambiaPasswordSiSeProporciona() {
        EditarUsuarioComando comando = new EditarUsuarioComando(1L, "user", "Nombre", "nueva", true, Set.of());
        Usuario usuario = new Usuario("user", "vieja", "Nombre");
        usuario.setId(1L);
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepositorio.guardar(any(Usuario.class))).thenReturn(usuario);

        servicio.editar(comando);

        assertNotEquals("vieja", usuario.getPassword());
        assertTrue(hasheadorContrasenas.verificar("nueva", usuario.getPassword()));
    }

    @Test
    void eliminar_admin_lanzaExcepcion() {
        Usuario admin = new Usuario("admin", "pass", "Admin");
        admin.setId(1L);
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(admin));

        assertThrows(IllegalArgumentException.class, () -> servicio.eliminar(1L));
    }

    @Test
    void eliminar_noAdmin_elimina() {
        Usuario usuario = new Usuario("user", "pass", "User");
        usuario.setId(2L);
        when(usuarioRepositorio.buscarPorId(2L)).thenReturn(Optional.of(usuario));

        servicio.eliminar(2L);

        verify(usuarioRepositorio).eliminarPorId(2L);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(usuarioRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.obtenerPorId(999L));
    }
}
