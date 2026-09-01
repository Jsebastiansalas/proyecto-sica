package com.acme.sica.aplicacion.incidente;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;
import com.acme.sica.dominio.puerto.salida.IncidenteRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.AfterEach;
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
class GestionarIncidenteServicioTest {

    @Mock
    private IncidenteRepositorioPuerto incidenteRepositorio;

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private GestionarIncidenteServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new GestionarIncidenteServicio(incidenteRepositorio, personaRepositorio, cadenaAutorizacion);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("registrar_incidente"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void crear_datosValidos_creaIncidente() {
        CrearIncidenteComando comando = new CrearIncidenteComando(1L, "Descripción", GravedadIncidente.ALTA);
        Persona persona = new Persona();
        persona.setId(1L);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));

        Incidente guardado = new Incidente(persona, null, "Descripción", GravedadIncidente.ALTA, null);
        guardado.setId(1L);
        when(incidenteRepositorio.guardar(any(Incidente.class))).thenReturn(guardado);

        Incidente resultado = servicio.crear(comando);

        assertEquals("Descripción", resultado.getDescripcion());
        assertEquals(GravedadIncidente.ALTA, resultado.getGravedad());
        assertEquals(persona, resultado.getPersona());
    }

    @Test
    void crear_personaNula_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(new CrearIncidenteComando(null, "Desc", GravedadIncidente.MEDIA)));
    }

    @Test
    void crear_descripcionVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(new CrearIncidenteComando(1L, "  ", GravedadIncidente.MEDIA)));
    }

    @Test
    void crear_personaNoExiste_lanzaExcepcion() {
        CrearIncidenteComando comando = new CrearIncidenteComando(999L, "Desc", GravedadIncidente.BAJA);
        when(personaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.crear(comando));
    }

    @Test
    void crear_sinSesion_lanzaExcepcion() {
        SesionContexto.cerrar();
        CrearIncidenteComando comando = new CrearIncidenteComando(1L, "Desc", GravedadIncidente.MEDIA);
        Persona persona = new Persona(); persona.setId(1L);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));

        assertThrows(IllegalStateException.class, () -> servicio.crear(comando));
    }

}
