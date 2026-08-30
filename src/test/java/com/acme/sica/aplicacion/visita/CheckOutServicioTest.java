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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckOutServicioTest {

    @Mock
    private VisitaRepositorioPuerto visitaRepositorio;

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;

    private CheckOutServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {
                // No-op para tests
            }
        };
        servicio = new CheckOutServicio(visitaRepositorio, personaRepositorio, cadenaAutorizacion);
    }

    @Test
    void checkOut_documentoEnBlanco_lanzaExcepcion() {
        // Given
        CheckOutComando comando = new CheckOutComando("  ");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.checkOut(comando));
        verifyNoInteractions(personaRepositorio, visitaRepositorio);
    }

    @Test
    void checkOut_personaNoExiste_lanzaExcepcion() {
        // Given
        CheckOutComando comando = new CheckOutComando("1234567890");
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.checkOut(comando));
        verify(personaRepositorio).buscarPorDocumento("1234567890");
    }

    @Test
    void checkOut_sinVisitaAbierta_lanzaExcepcion() {
        // Given
        CheckOutComando comando = new CheckOutComando("1234567890");
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Invitado", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.of(persona));
        when(visitaRepositorio.buscarVisitaAbiertaPorPersona(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalStateException.class, () -> servicio.checkOut(comando));
    }

    @Test
    void checkOut_exitoso_marcaVisitaComoCerrada() {
        // Given
        CheckOutComando comando = new CheckOutComando("1234567890");
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Invitado", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("1234567890")).thenReturn(Optional.of(persona));

        Visita visitaAbierta = new Visita();
        visitaAbierta.setId(1L);
        visitaAbierta.setPersona(persona);
        visitaAbierta.setEstado(EstadoVisita.DENTRO);
        visitaAbierta.setFechaHoraIngreso(LocalDateTime.now().minusHours(2));
        when(visitaRepositorio.buscarVisitaAbiertaPorPersona(1L)).thenReturn(Optional.of(visitaAbierta));

        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.checkOut(comando);

        // Then
        assertEquals(EstadoVisita.CERRADA, resultado.getEstado());
        assertNotNull(resultado.getFechaHoraSalida());
        verify(visitaRepositorio).guardar(argThat(v -> 
            v.getEstado() == EstadoVisita.CERRADA && v.getFechaHoraSalida() != null
        ));
    }

    @Test
    void listarDentro_retornaLista() {
        // Given
        Visita visita1 = new Visita();
        visita1.setEstado(EstadoVisita.DENTRO);
        Visita visita2 = new Visita();
        visita2.setEstado(EstadoVisita.DENTRO);
        when(visitaRepositorio.listarTodos()).thenReturn(List.of(visita1, visita2));

        // When
        List<Visita> resultado = servicio.listarDentro();

        // Then
        assertEquals(2, resultado.size());
    }
}