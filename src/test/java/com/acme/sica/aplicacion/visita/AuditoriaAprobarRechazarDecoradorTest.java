package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.AprobarRechazarVisitaCasoUso;
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
class AuditoriaAprobarRechazarDecoradorTest {

    @Mock
    private AprobarRechazarVisitaCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaAprobarRechazarDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaAprobarRechazarDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("aprobar_rechazar"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void aprobar_delegaYRegistraAuditoria() {
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.aprobar(1L)).thenReturn(visita);

        Visita resultado = decorador.aprobar(1L);

        assertEquals(visita, resultado);
        verify(decorado).aprobar(1L);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void rechazar_delegaYRegistraAuditoria() {
        RechazarVisitaComando comando = new RechazarVisitaComando(1L, "Motivo");
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Visita visita = new Visita();
        visita.setId(1L);
        visita.setPersona(persona);
        when(decorado.rechazar(comando)).thenReturn(visita);

        Visita resultado = decorador.rechazar(comando);

        assertEquals(visita, resultado);
        verify(decorado).rechazar(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

}
