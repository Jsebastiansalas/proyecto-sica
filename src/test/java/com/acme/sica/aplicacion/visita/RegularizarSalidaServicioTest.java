package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularizarSalidaServicioTest {

    @Mock
    private VisitaRepositorioPuerto visitaRepositorio;

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    @Mock
    private EstrategiaSalidaOlvidada estrategia;

    private ManejadorAutorizacion cadenaAutorizacion;
    private RegularizarSalidaServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new RegularizarSalidaServicio(visitaRepositorio, personaRepositorio, cadenaAutorizacion,
                Map.of(TipoRegularizacion.CIERRE_SISTEMA, estrategia));
    }

    @Test
    void regularizar_visitaAbierta_ejecutaEstrategia() {
        RegularizarSalidaComando comando = new RegularizarSalidaComando("123", TipoRegularizacion.CIERRE_SISTEMA, "Motivo");
        Persona persona = new Persona();
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("123")).thenReturn(Optional.of(persona));

        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        visita.setEstado(EstadoVisita.DENTRO);
        when(visitaRepositorio.buscarVisitaAbiertaPorPersona(1L)).thenReturn(Optional.of(visita));

        Visita regularizada = new Visita();
        regularizada.setId(1L);
        regularizada.setEstado(EstadoVisita.CERRADA_POR_SISTEMA);
        when(estrategia.regularizar(visita, "Motivo")).thenReturn(regularizada);

        Visita resultado = servicio.regularizar(comando);

        assertEquals(EstadoVisita.CERRADA_POR_SISTEMA, resultado.getEstado());
        verify(estrategia).regularizar(visita, "Motivo");
    }

    @Test
    void regularizar_personaNoExiste_lanzaExcepcion() {
        RegularizarSalidaComando comando = new RegularizarSalidaComando("123", TipoRegularizacion.CIERRE_SISTEMA, null);
        when(personaRepositorio.buscarPorDocumento("123")).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.regularizar(comando));
    }

    @Test
    void regularizar_sinVisitaAbierta_lanzaExcepcion() {
        RegularizarSalidaComando comando = new RegularizarSalidaComando("123", TipoRegularizacion.CIERRE_SISTEMA, null);
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombreCompleto("Carlos");
        when(personaRepositorio.buscarPorDocumento("123")).thenReturn(Optional.of(persona));
        when(visitaRepositorio.buscarVisitaAbiertaPorPersona(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> servicio.regularizar(comando));
    }

}
