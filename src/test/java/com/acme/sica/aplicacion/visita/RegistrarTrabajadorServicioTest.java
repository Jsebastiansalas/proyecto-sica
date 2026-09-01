package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PersonaBloqueadaExcepcion;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarTrabajadorServicioTest {

    @Mock
    private VisitaRepositorioPuerto visitaRepositorio;

    @Mock
    private PersonaRepositorioPuerto personaRepositorio;

    @Mock
    private FuncionarioRepositorioPuerto funcionarioRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private RegistrarTrabajadorServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new RegistrarTrabajadorServicio(visitaRepositorio, personaRepositorio, funcionarioRepositorio, cadenaAutorizacion);
    }

    @Test
    void registrar_personaNueva_creaTrabajadorYVisita() {
        RegistrarTrabajadorComando comando = new RegistrarTrabajadorComando("123", "Luis", null, 1L);
        when(personaRepositorio.buscarPorDocumento("123")).thenReturn(Optional.empty());

        Persona personaGuardada = new Persona(TipoPersona.TRABAJADOR, "123", "Luis", null);
        personaGuardada.setId(1L);
        when(personaRepositorio.guardar(any(Persona.class))).thenReturn(personaGuardada);

        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setActivo(true);
        when(funcionarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(funcionario));

        when(visitaRepositorio.guardar(any(Visita.class))).thenAnswer(inv -> inv.getArgument(0));

        Visita resultado = servicio.registrar(comando);

        assertEquals(EstadoVisita.PENDIENTE_APROBACION_OLVIDO, resultado.getEstado());
    }

    @Test
    void registrar_personaBloqueada_lanzaExcepcion() {
        RegistrarTrabajadorComando comando = new RegistrarTrabajadorComando("123", "Luis", null, 1L);
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setBloqueada(true);
        persona.setNombreCompleto("Luis");
        when(personaRepositorio.buscarPorDocumento("123")).thenReturn(Optional.of(persona));

        assertThrows(PersonaBloqueadaExcepcion.class, () -> servicio.registrar(comando));
    }

    @Test
    void registrar_funcionarioNoExiste_lanzaExcepcion() {
        RegistrarTrabajadorComando comando = new RegistrarTrabajadorComando("123", "Luis", null, 999L);
        Persona persona = new Persona();
        persona.setId(1L);
        when(personaRepositorio.buscarPorDocumento("123")).thenReturn(Optional.of(persona));
        when(funcionarioRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.registrar(comando));
    }

    @Test
    void registrar_nombreVacio_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(new RegistrarTrabajadorComando("123", "  ", null, 1L)));
    }
}
