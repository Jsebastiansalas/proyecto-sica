package com.acme.sica.aplicacion.empresa;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.puerto.entrada.GestionarEmpresaCasoUso;
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
class AuditoriaEmpresaDecoradorTest {

    @Mock
    private GestionarEmpresaCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaEmpresaDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaEmpresaDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("gestionar_empresas"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        CrearEmpresaComando comando = new CrearEmpresaComando("Acme", "Quito");
        Empresa empresa = new Empresa("Acme", "Quito");
        empresa.setId(1L);
        when(decorado.crear(comando)).thenReturn(empresa);

        Empresa resultado = decorador.crear(comando);

        assertEquals(empresa, resultado);
        verify(decorado).crear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void editar_delegaYRegistraAuditoria() {
        EditarEmpresaComando comando = new EditarEmpresaComando(1L, "Acme", "Guayaquil", true);
        Empresa empresa = new Empresa("Acme", "Guayaquil");
        empresa.setId(1L);
        when(decorado.editar(comando)).thenReturn(empresa);

        Empresa resultado = decorador.editar(comando);

        assertEquals(empresa, resultado);
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
