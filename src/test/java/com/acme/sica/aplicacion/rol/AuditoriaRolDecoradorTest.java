package com.acme.sica.aplicacion.rol;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
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
class AuditoriaRolDecoradorTest {

    @Mock
    private GestionarRolCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaRolDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaRolDecorador(decorado, bitacoraRepositorio);
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        // Given
        CrearRolComando comando = new CrearRolComando("NUEVO_ROL", "Descripción");
        Rol rolCreado = new Rol("NUEVO_ROL", "Descripción");
        rolCreado.setId(1L);
        when(decorado.crear(comando)).thenReturn(rolCreado);

        // When
        Rol resultado = decorador.crear(comando);

        // Then
        assertEquals(rolCreado, resultado);
        verify(decorado).crear(comando);

        ArgumentCaptor<String> captorAccion = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorEntidad = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> captorEntidadId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> captorDetalle = ArgumentCaptor.forClass(String.class);

        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void editar_delegaYRegistraAuditoria() {
        // Given
        EditarRolComando comando = new EditarRolComando(1L, "ROL_EDITADO", "Nueva descripción");
        Rol rolEditado = new Rol("ROL_EDITADO", "Nueva descripción");
        rolEditado.setId(1L);
        when(decorado.editar(comando)).thenReturn(rolEditado);

        // When
        Rol resultado = decorador.editar(comando);

        // Then
        assertEquals(rolEditado, resultado);
        verify(decorado).editar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void eliminar_delegaYRegistraAuditoria() {
        // Given
        Long rolId = 1L;
        doNothing().when(decorado).eliminar(rolId);

        // When
        decorador.eliminar(rolId);

        // Then
        verify(decorado).eliminar(rolId);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void asignarRoles_delegaYRegistraAuditoria() {
        // Given
        AsignarRolesUsuarioComando comando = new AsignarRolesUsuarioComando(1L, Set.of(2L, 3L));
        doNothing().when(decorado).asignarRoles(comando);

        // When
        decorador.asignarRoles(comando);

        // Then
        verify(decorado).asignarRoles(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void listarTodos_delegaSinAuditoria() {
        // Given
        List<Rol> roles = Collections.emptyList();
        when(decorado.listarTodos()).thenReturn(roles);

        // When
        List<Rol> resultado = decorador.listarTodos();

        // Then
        assertEquals(roles, resultado);
        verify(decorado).listarTodos();
        verify(bitacoraRepositorio, never()).guardar(any());
    }

    @Test
    void obtenerPorId_delegaSinAuditoria() {
        // Given
        Rol rol = new Rol("TEST", "Test");
        rol.setId(1L);
        when(decorado.obtenerPorId(1L)).thenReturn(rol);

        // When
        Rol resultado = decorador.obtenerPorId(1L);

        // Then
        assertEquals(rol, resultado);
        verify(decorado).obtenerPorId(1L);
        verify(bitacoraRepositorio, never()).guardar(any());
    }
}