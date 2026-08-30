package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInInvitadoServicioTest {

    @Mock
    private VisitaRepositorioPuerto visitaRepositorio;

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private EstrategiaCierreSistema estrategiaCierreSistema;

    private CheckInInvitadoServicio servicio;

    @BeforeEach
    void setUp() {
        // Implementación simple que no hace nada (permite todo)
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {
                // No-op para tests
            }
        };
        // Usar instancia real de la estrategia
        estrategiaCierreSistema = new EstrategiaCierreSistema(visitaRepositorio);
        servicio = new CheckInInvitadoServicio(visitaRepositorio, personaRepositorio, cadenaAutorizacion, estrategiaCierreSistema);
    }

    @Test
    void checkIn_documentoEnBlanco_lanzaExcepcion() {
        // Given
        CheckInInvitadoComando comando = new CheckInInvitadoComando("  ");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.checkIn(comando));
        verifyNoInteractions(personaRepositorio, visitaRepositorio);
    }

    @Test
    void checkIn_personaNoExiste_lanzaExcepcion() {
        // Given
        CheckInInvitadoComando comando = new CheckInInvitadoComando("1234567890");
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.checkIn(comando));
        verify(personaRepositorio).buscarPorDocumento("1234567890");
    }

    @Test
    void checkIn_personaBloqueada_lanzaExcepcion() {
        // Given
        CheckInInvitadoComando comando = new CheckInInvitadoComando("1234567890");
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Invitado", null);
        persona.setBloqueada(true);
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.of(persona));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.checkIn(comando));
    }

    @Test
    void checkIn_sinVisitaAprobada_lanzaExcepcion() {
        // Given
        CheckInInvitadoComando comando = new CheckInInvitadoComando("1234567890");
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Invitado", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.of(persona));
        when(visitaRepositorio.buscarPorPersonaYEstado(1L, EstadoVisita.APROBADO)).thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(IllegalStateException.class, () -> servicio.checkIn(comando));
    }

    @Test
    void checkIn_exitoso_marcaVisitaComoDentro() {
        // Given
        CheckInInvitadoComando comando = new CheckInInvitadoComando("1234567890");
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Invitado", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.of(persona));

        Visita visitaAprobada = new Visita();
        visitaAprobada.setId(1L);
        visitaAprobada.setPersona(persona);
        visitaAprobada.setEstado(EstadoVisita.APROBADO);
        when(visitaRepositorio.buscarPorPersonaYEstado(1L, EstadoVisita.APROBADO)).thenReturn(List.of(visitaAprobada));

        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.checkIn(comando);

        // Then
        assertEquals(EstadoVisita.DENTRO, resultado.getEstado());
        assertNotNull(resultado.getFechaHoraIngreso());
        verify(visitaRepositorio).guardar(argThat(v -> 
            v.getEstado() == EstadoVisita.DENTRO && v.getFechaHoraIngreso() != null
        ));
    }

    @Test
    void checkIn_conVisitaAbierta_cierraVisitaAnteriorYCreaNueva() {
        // Given
        CheckInInvitadoComando comando = new CheckInInvitadoComando("1234567890");
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Invitado", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.of(persona));

        // Visita abierta (DENTRO) que debe cerrarse automáticamente
        Visita visitaAbierta = new Visita();
        visitaAbierta.setId(1L);
        visitaAbierta.setPersona(persona);
        visitaAbierta.setEstado(EstadoVisita.DENTRO);
        visitaAbierta.setFechaHoraIngreso(LocalDateTime.now().minusHours(2));
        when(visitaRepositorio.buscarVisitaAbiertaPorPersona(1L)).thenReturn(Optional.of(visitaAbierta));

        // Visita aprobada para el nuevo check-in
        Visita visitaAprobada = new Visita();
        visitaAprobada.setId(2L);
        visitaAprobada.setPersona(persona);
        visitaAprobada.setEstado(EstadoVisita.APROBADO);
        when(visitaRepositorio.buscarPorPersonaYEstado(1L, EstadoVisita.APROBADO)).thenReturn(List.of(visitaAprobada));

        Visita visitaGuardada = new Visita();
        visitaGuardada.setId(2L);
        visitaGuardada.setPersona(persona);
        visitaGuardada.setEstado(EstadoVisita.DENTRO);
        when(visitaRepositorio.guardar(any(Visita.class))).thenReturn(visitaGuardada);

        // When
        Visita resultado = servicio.checkIn(comando);

        // Then
        assertEquals(EstadoVisita.DENTRO, resultado.getEstado());
        // Verificar que se cerró la visita anterior (se guardó con estado CERRADA_POR_SISTEMA)
        verify(visitaRepositorio).guardar(argThat(v -> 
            v.getEstado() == EstadoVisita.CERRADA_POR_SISTEMA && v.getId().equals(1L)
        ));
        // Verificar que se guardó la nueva visita
        verify(visitaRepositorio).guardar(argThat(v -> 
            v.getEstado() == EstadoVisita.DENTRO && v.getId().equals(2L)
        ));
    }
}