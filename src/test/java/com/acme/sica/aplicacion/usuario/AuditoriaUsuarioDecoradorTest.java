package com.acme.sica.aplicacion.usuario;

import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarUsuarioCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaUsuarioDecoradorTest {

    @Mock
    private GestionarUsuarioCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaUsuarioDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaUsuarioDecorador(decorado, bitacoraRepositorio);
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        // Given
        CrearUsuarioComando comando = new CrearUsuarioComando("nuevo", "pass123", "Nuevo Usuario", Set.of(1L));
        Usuario usuarioCreado = new Usuario("nuevo", "hash", "Nuevo Usuario");
        usuarioCreado.setId(1L);
        when(decorado.crear(comando)).thenReturn(usuarioCreado);

        // When
        Usuario resultado = decorador.crear(comando);

        // Then
        assertEquals(usuarioCreado, resultado);
        verify(decorado).crear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void editar_delegaYRegistraAuditoria() {
        // Given
        EditarUsuarioComando comando = new EditarUsuarioComando(1L, "editado", "Usuario Editado", "newpass", true, Set.of(2L));
        Usuario usuarioEditado = new Usuario("editado", "hash", "Usuario Editado");
        usuarioEditado.setId(1L);
        when(decorado.editar(comando)).thenReturn(usuarioEditado);

        // When
        Usuario resultado = decorador.editar(comando);

        // Then
        assertEquals(usuarioEditado, resultado);
        verify(decorado).editar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void eliminar_delegaYRegistraAuditoria() {
        // Given
        Long usuarioId = 1L;
        doNothing().when(decorado).eliminar(usuarioId);

        // When
        decorador.eliminar(usuarioId);

        // Then
        verify(decorado).eliminar(usuarioId);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void listarTodos_delegaSinAuditoria() {
        // Given
        List<Usuario> usuarios = Collections.emptyList();
        when(decorado.listarTodos()).thenReturn(usuarios);

        // When
        List<Usuario> resultado = decorador.listarTodos();

        // Then
        assertEquals(usuarios, resultado);
        verify(decorado).listarTodos();
        verify(bitacoraRepositorio, never()).guardar(any());
    }

    @Test
    void obtenerPorId_delegaSinAuditoria() {
        // Given
        Usuario usuario = new Usuario("test", "hash", "Test");
        usuario.setId(1L);
        when(decorado.obtenerPorId(1L)).thenReturn(usuario);

        // When
        Usuario resultado = decorador.obtenerPorId(1L);

        // Then
        assertEquals(usuario, resultado);
        verify(decorado).obtenerPorId(1L);
        verify(bitacoraRepositorio, never()).guardar(any());
    }
}