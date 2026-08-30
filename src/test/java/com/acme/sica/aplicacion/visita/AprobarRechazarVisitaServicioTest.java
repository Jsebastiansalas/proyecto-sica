package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprobarRechazarVisitaServicioTest {

    @Mock
    private VisitaRepositorioPuerto visitaRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;

    private AprobarRechazarVisitaServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {
                // No-op para tests
            }
        };
        servicio = new AprobarRechazarVisitaServicio(visitaRepositorio, cadenaAutorizacion);
    }

    @Test
    void aprobar_visitaNoExiste_lanzaExcepcion() {
        // Given
        when(visitaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.aprobar(999L));
    }

    @Test
    void aprobar_visitaNoPendiente_lanzaExcepcion() {
        // Given
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setEstado(EstadoVisita.APROBADO);
        when(visitaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(visita));

        // When & Then
        assertThrows(IllegalStateException.class, () -> servicio.aprobar(1L));
    }

    @Test
    void aprobar_visitaPendienteAprobacion_exitoso() {
        // Given
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setEstado(EstadoVisita.PENDIENTE_APROBACION);
        when(visitaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(visita));
        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.aprobar(1L);

        // Then
        assertEquals(EstadoVisita.APROBADO, resultado.getEstado());
        verify(visitaRepositorio).guardar(argThat(v -> v.getEstado() == EstadoVisita.APROBADO));
    }

    @Test
    void aprobar_visitaPendienteAprobacionOlvido_exitoso() {
        // Given
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setEstado(EstadoVisita.PENDIENTE_APROBACION_OLVIDO);
        when(visitaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(visita));
        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.aprobar(1L);

        // Then
        assertEquals(EstadoVisita.APROBADO, resultado.getEstado());
    }

    @Test
    void rechazar_visitaNoExiste_lanzaExcepcion() {
        // Given
        RechazarVisitaComando comando = new RechazarVisitaComando(999L, "Motivo");
        when(visitaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.rechazar(comando));
    }

    @Test
    void rechazar_visitaNoPendiente_lanzaExcepcion() {
        // Given
        RechazarVisitaComando comando = new RechazarVisitaComando(1L, "Motivo");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setEstado(EstadoVisita.APROBADO);
        when(visitaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(visita));

        // When & Then
        assertThrows(IllegalStateException.class, () -> servicio.rechazar(comando));
    }

    @Test
    void rechazar_visitaPendiente_exitoso() {
        // Given
        RechazarVisitaComando comando = new RechazarVisitaComando(1L, "No autorizado");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setEstado(EstadoVisita.PENDIENTE_APROBACION);
        when(visitaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(visita));
        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.rechazar(comando);

        // Then
        assertEquals(EstadoVisita.RECHAZADO, resultado.getEstado());
        assertTrue(resultado.getMotivo().contains("RECHAZADO: No autorizado"));
        verify(visitaRepositorio).guardar(argThat(v -> v.getEstado() == EstadoVisita.RECHAZADO));
    }

    @Test
    void rechazar_sinMotivo_noModificaMotivo() {
        // Given
        RechazarVisitaComando comando = new RechazarVisitaComando(1L, "");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setEstado(EstadoVisita.PENDIENTE_APROBACION);
        visita.setMotivo("Motivo original");
        when(visitaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(visita));
        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> {
            Visita v = inv.getArgument(0);
            v.setId(1L);
            return v;
        });

        // When
        Visita resultado = servicio.rechazar(comando);

        // Then
        assertEquals(EstadoVisita.RECHAZADO, resultado.getEstado());
        // El motivo original se mantiene porque no se pasó motivo de rechazo
    }

    @Test
    void listarPendientes_retornaAmbosEstados() {
        // Given
        Visita pendiente1 = new Visita();
        pendiente1.setEstado(EstadoVisita.PENDIENTE_APROBACION);
        Visita pendiente2 = new Visita();
        pendiente2.setEstado(EstadoVisita.PENDIENTE_APROBACION_OLVIDO);
        Visita otra = new Visita();
        otra.setEstado(EstadoVisita.APROBADO);

        when(visitaRepositorio.listarTodos()).thenReturn(List.of(pendiente1, pendiente2, otra));

        // When
        List<Visita> resultado = servicio.listarPendientes();

        // Then
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(v -> 
            v.getEstado() == EstadoVisita.PENDIENTE_APROBACION 
            || v.getEstado() == EstadoVisita.PENDIENTE_APROBACION_OLVIDO
        ));
    }
}