package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.CheckOutCasoUso;
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
class AuditoriaCheckOutDecoradorTest {

    @Mock
    private CheckOutCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaCheckOutDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaCheckOutDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("check_out"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void checkOut_delegaYRegistraAuditoria() {
        CheckOutComando comando = new CheckOutComando("123");
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.checkOut(comando)).thenReturn(visita);

        Visita resultado = decorador.checkOut(comando);

        assertEquals(visita, resultado);
        verify(decorado).checkOut(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

}
