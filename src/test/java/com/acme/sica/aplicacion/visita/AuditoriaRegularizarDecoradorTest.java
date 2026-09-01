package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.RegularizarSalidaCasoUso;
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
class AuditoriaRegularizarDecoradorTest {

    @Mock
    private RegularizarSalidaCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaRegularizarDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaRegularizarDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("regularizar_salida"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void regularizar_cierreSistema_delegaYRegistraAuditoria() {
        RegularizarSalidaComando comando = new RegularizarSalidaComando("123", TipoRegularizacion.CIERRE_SISTEMA, "Motivo");
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.regularizar(comando)).thenReturn(visita);

        Visita resultado = decorador.regularizar(comando);

        assertEquals(visita, resultado);
        verify(decorado).regularizar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void regularizar_nuevoIngreso_delegaYRegistraAuditoria() {
        RegularizarSalidaComando comando = new RegularizarSalidaComando("123", TipoRegularizacion.NUEVO_INGRESO, "Motivo");
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.regularizar(comando)).thenReturn(visita);

        decorador.regularizar(comando);

        verify(decorado).regularizar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

}
