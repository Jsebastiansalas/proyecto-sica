package com.acme.sica.aplicacion.funcionario;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.puerto.entrada.GestionarFuncionarioCasoUso;
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
class AuditoriaFuncionarioDecoradorTest {

    @Mock
    private GestionarFuncionarioCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaFuncionarioDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaFuncionarioDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("gestionar_funcionarios"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        CrearFuncionarioComando comando = new CrearFuncionarioComando(null, 1L, "Juan", "Guardia");
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNombreCompleto("Juan");
        when(decorado.crear(comando)).thenReturn(funcionario);

        Funcionario resultado = decorador.crear(comando);

        assertEquals(funcionario, resultado);
        verify(decorado).crear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void editar_delegaYRegistraAuditoria() {
        EditarFuncionarioComando comando = new EditarFuncionarioComando(1L, 1L, 2L, "Pedro", "Supervisor", true);
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        funcionario.setNombreCompleto("Pedro");
        when(decorado.editar(comando)).thenReturn(funcionario);

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
