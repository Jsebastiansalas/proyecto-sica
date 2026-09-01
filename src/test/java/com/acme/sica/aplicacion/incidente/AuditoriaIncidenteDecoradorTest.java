package com.acme.sica.aplicacion.incidente;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;
import com.acme.sica.dominio.puerto.entrada.GestionarIncidenteCasoUso;
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
class AuditoriaIncidenteDecoradorTest {

    @Mock
    private GestionarIncidenteCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaIncidenteDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaIncidenteDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("registrar_incidente"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        CrearIncidenteComando comando = new CrearIncidenteComando(1L, "Desc", GravedadIncidente.ALTA);
        Persona persona = new Persona();
        persona.setNombreCompleto("Carlos");
        Incidente incidente = new Incidente(persona, null, "Desc", GravedadIncidente.ALTA, null);
        incidente.setId(1L);
        when(decorado.crear(comando)).thenReturn(incidente);

        Incidente resultado = decorador.crear(comando);

        assertEquals(incidente, resultado);
        verify(decorado).crear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

}
