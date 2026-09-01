package com.acme.sica.aplicacion.persona;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarPersonaServicioTest {

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private GestionarPersonaServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new GestionarPersonaServicio(personaRepositorio, cadenaAutorizacion);
    }

    @Test
    void crear_documentoUnico_creaPersona() {
        CrearPersonaComando comando = new CrearPersonaComando(TipoPersona.INVITADO, "1234567890", "Carlos Perez", "http://foto.jpg");
        when(personaRepositorio.existePorDocumento("1234567890")).thenReturn(false);

        Persona personaGuardada = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Perez", "http://foto.jpg");
        personaGuardada.setId(1L);
        when(personaRepositorio.guardar(any(Persona.class))).thenReturn(personaGuardada);

        Persona resultado = servicio.crear(comando);

        assertEquals("1234567890", resultado.getDocumentoIdentidad());
        assertEquals("Carlos Perez", resultado.getNombreCompleto());
        assertEquals(TipoPersona.INVITADO, resultado.getTipo());
    }

    @Test
    void crear_documentoDuplicado_lanzaExcepcion() {
        CrearPersonaComando comando = new CrearPersonaComando(TipoPersona.INVITADO, "1234567890", "Carlos Perez", null);
        when(personaRepositorio.existePorDocumento("1234567890")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
    }

    @Test
    void crear_camposObligatoriosVacios_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(new CrearPersonaComando(TipoPersona.INVITADO, "  ", "Nombre", null)));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(new CrearPersonaComando(TipoPersona.INVITADO, "123", "  ", null)));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.crear(new CrearPersonaComando(null, "123", "Nombre", null)));
    }

    @Test
    void editar_cambiaDatos() {
        EditarPersonaComando comando = new EditarPersonaComando(1L, "9998887776", "Ana Lopez", "foto.jpg", TipoPersona.TRABAJADOR, false);
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Perez", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));
        when(personaRepositorio.existePorDocumento("9998887776")).thenReturn(false);

        Persona guardada = new Persona(TipoPersona.TRABAJADOR, "9998887776", "Ana Lopez", "foto.jpg");
        guardada.setId(1L);
        when(personaRepositorio.guardar(any(Persona.class))).thenReturn(guardada);

        Persona resultado = servicio.editar(comando);

        assertEquals("9998887776", resultado.getDocumentoIdentidad());
        assertEquals("Ana Lopez", resultado.getNombreCompleto());
        assertEquals(TipoPersona.TRABAJADOR, resultado.getTipo());
    }

    @Test
    void editar_mismoDocumento_noVerificaDuplicado() {
        EditarPersonaComando comando = new EditarPersonaComando(1L, "1234567890", "Carlos Perez", null, TipoPersona.INVITADO, false);
        Persona persona = new Persona(TipoPersona.INVITADO, "1234567890", "Carlos Perez", null);
        persona.setId(1L);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));

        servicio.editar(comando);

        verify(personaRepositorio, never()).existePorDocumento(anyString());
    }

    @Test
    void eliminar_existente_elimina() {
        Persona persona = new Persona();
        persona.setId(1L);
        when(personaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(persona));

        servicio.eliminar(1L);

        verify(personaRepositorio).eliminarPorId(1L);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(personaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.obtenerPorId(999L));
    }
}
