package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreRegistrarInvitadoServicioTest {

    @Mock
    private VisitaRepositorioPuerto visitaRepositorio;

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    @Mock
    private FuncionarioRepositorioPuerto funcionarioRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;

    private PreRegistrarInvitadoServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {
                // No-op para tests
            }
        };
        servicio = new PreRegistrarInvitadoServicio(visitaRepositorio, personaRepositorio, funcionarioRepositorio, cadenaAutorizacion);
    }

    @Test
    void preRegistrar_sinPersonaId_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                null, 1L, LocalDateTime.now().plusHours(1), "Motivo");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_sinFuncionarioId_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, null, LocalDateTime.now().plusHours(1), "Motivo");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_sinFechaHoraEsperada_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 1L, null, "Motivo");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_fechaPasada_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 1L, LocalDateTime.now().minusHours(1), "Motivo");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_personaNoExiste_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                999L, 1L, LocalDateTime.now().plusHours(1), "Motivo");
        when(personaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_personaNoEsInvitado_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 1L, LocalDateTime.now().plusHours(1), "Motivo");
        Persona persona = new Persona(TipoPersona.TRABAJADOR, "123", "Trabajador", null);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_personaBloqueada_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 1L, LocalDateTime.now().plusHours(1), "Motivo");
        Persona persona = new Persona(TipoPersona.INVITADO, "123", "Invitado", null);
        persona.setBloqueada(true);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_funcionarioNoExiste_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 999L, LocalDateTime.now().plusHours(1), "Motivo");
        Persona persona = new Persona(TipoPersona.INVITADO, "123", "Invitado", null);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));
        when(funcionarioRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_funcionarioInactivo_lanzaExcepcion() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 1L, LocalDateTime.now().plusHours(1), "Motivo");
        Persona persona = new Persona(TipoPersona.INVITADO, "123", "Invitado", null);
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setActivo(false);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));
        when(funcionarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(funcionario));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.preRegistrar(comando));
    }

    @Test
    void preRegistrar_exitoso_creaVisitaAprobada() {
        // Given
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(
                1L, 1L, LocalDateTime.now().plusHours(1), "Visita de prueba");
        Persona persona = new Persona(TipoPersona.INVITADO, "123", "Invitado", null);
        persona.setId(1L);
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setActivo(true);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));
        when(funcionarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(funcionario));

        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.preRegistrar(comando);

        // Then
        assertEquals(EstadoVisita.APROBADO, resultado.getEstado());
        assertEquals(persona, resultado.getPersona());
        assertEquals(funcionario, resultado.getFuncionario());
        assertEquals("Visita de prueba", resultado.getMotivo());
        assertNotNull(resultado.getFechaHoraEsperada());
        verify(visitaRepositorio).guardar(argThat(v -> 
            v.getEstado() == EstadoVisita.APROBADO && v.getPersona().getId().equals(1L)
        ));
    }
}