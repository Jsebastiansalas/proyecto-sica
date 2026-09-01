package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.PreRegistrarInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditoriaPreRegistrarInvitadoDecoradorTest {

    @Mock
    private PreRegistrarInvitadoCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaPreRegistrarInvitadoDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaPreRegistrarInvitadoDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("pre_registrar_invitado"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void preRegistrar_delegaYRegistraAuditoria() {
        PreRegistrarInvitadoComando comando = new PreRegistrarInvitadoComando(1L, 1L, LocalDateTime.now(), "Visita");
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.preRegistrar(comando)).thenReturn(visita);

        Visita resultado = decorador.preRegistrar(comando);

        assertEquals(visita, resultado);
        verify(decorado).preRegistrar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }
}
