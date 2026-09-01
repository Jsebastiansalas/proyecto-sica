package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.RegistrarNoAnunciadoCasoUso;
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
class AuditoriaRegistrarNoAnunciadoDecoradorTest {

    @Mock
    private RegistrarNoAnunciadoCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaRegistrarNoAnunciadoDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaRegistrarNoAnunciadoDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("registrar_no_anunciado"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void registrar_delegaYRegistraAuditoria() {
        RegistrarNoAnunciadoComando comando = new RegistrarNoAnunciadoComando("123", "Carlos", null, 1L, "Visita");
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.registrar(comando)).thenReturn(visita);

        Visita resultado = decorador.registrar(comando);

        assertEquals(visita, resultado);
        verify(decorado).registrar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }
}
