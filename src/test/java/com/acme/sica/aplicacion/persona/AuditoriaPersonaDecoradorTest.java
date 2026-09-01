package com.acme.sica.aplicacion.persona;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.entrada.GestionarPersonaCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaPersonaDecoradorTest {

    @Mock
    private GestionarPersonaCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaPersonaDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaPersonaDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("registrar_persona"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        CrearPersonaComando comando = new CrearPersonaComando(null, null, null, null);
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombreCompleto("Carlos");
        when(decorado.crear(comando)).thenReturn(persona);

        Persona resultado = decorador.crear(comando);

        assertEquals(persona, resultado);
        verify(decorado).crear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void editar_delegaYRegistraAuditoria() {
        EditarPersonaComando comando = new EditarPersonaComando(1L, "123", "Carlos", null, TipoPersona.INVITADO, false);
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombreCompleto("Carlos");
        when(decorado.editar(comando)).thenReturn(persona);

        decorador.editar(comando);

        verify(decorado).editar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void eliminar_delegaYRegistraAuditoria() {
        decorador.eliminar(1L);

        verify(decorado).eliminar(1L);
        verify(bitacoraRepositorio).guardar(any());
    }

}
