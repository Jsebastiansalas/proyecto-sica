package com.acme.sica.aplicacion.persona;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.puerto.entrada.GestionarBloqueoPersonaCasoUso;
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
class AuditoriaBloqueoPersonaDecoradorTest {

    @Mock
    private GestionarBloqueoPersonaCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaBloqueoPersonaDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaBloqueoPersonaDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("bloquear_persona"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void bloquear_delegaYRegistraAuditoria() {
        BloquearPersonaComando comando = new BloquearPersonaComando(1L, "Motivo");
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombreCompleto("Carlos");
        when(decorado.bloquear(comando)).thenReturn(persona);

        Persona resultado = decorador.bloquear(comando);

        assertEquals(persona, resultado);
        verify(decorado).bloquear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void desbloquear_delegaYRegistraAuditoria() {
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombreCompleto("Carlos");
        when(decorado.desbloquear(1L)).thenReturn(persona);

        decorador.desbloquear(1L);

        verify(decorado).desbloquear(1L);
        verify(bitacoraRepositorio).guardar(any());
    }

}
