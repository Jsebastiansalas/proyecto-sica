package com.acme.sica.aplicacion.funcionario;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.salida.EmpresaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarFuncionarioServicioTest {

    @Mock
    private FuncionarioRepositorioPuerto funcionarioRepositorio;

    @Mock
    private EmpresaRepositorioPuerto empresaRepositorio;

    @Mock
    private UsuarioRepositorioPuerto usuarioRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private GestionarFuncionarioServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new GestionarFuncionarioServicio(funcionarioRepositorio, empresaRepositorio, usuarioRepositorio, cadenaAutorizacion);
    }

    @Test
    void crear_sinUsuario_creaFuncionario() {
        CrearFuncionarioComando comando = new CrearFuncionarioComando(null, 1L, "Juan Perez", "Guardia");
        Empresa empresa = new Empresa("Acme", "Quito");
        empresa.setId(1L);
        when(empresaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(empresa));

        Funcionario funcionarioGuardado = new Funcionario(null, empresa, "Juan Perez", "Guardia");
        funcionarioGuardado.setId(1L);
        when(funcionarioRepositorio.guardar(any(Funcionario.class))).thenReturn(funcionarioGuardado);

        Funcionario resultado = servicio.crear(comando);

        assertEquals("Juan Perez", resultado.getNombreCompleto());
        assertEquals("Guardia", resultado.getCargo());
        assertEquals(empresa, resultado.getEmpresa());
    }

    @Test
    void crear_nombreVacio_lanzaExcepcion() {
        CrearFuncionarioComando comando = new CrearFuncionarioComando(null, 1L, "  ", "Guardia");

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
    }

    @Test
    void crear_empresaInactiva_lanzaExcepcion() {
        CrearFuncionarioComando comando = new CrearFuncionarioComando(null, 1L, "Juan Perez", "Guardia");
        Empresa empresa = new Empresa("Acme", "Quito");
        empresa.setId(1L);
        empresa.setActiva(false);
        when(empresaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(empresa));

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
    }

    @Test
    void crear_usuarioYaFuncionario_lanzaExcepcion() {
        CrearFuncionarioComando comando = new CrearFuncionarioComando(2L, 1L, "Juan Perez", "Guardia");
        Empresa empresa = new Empresa("Acme", "Quito");
        empresa.setId(1L);
        when(empresaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(empresa));

        Usuario usuario = new Usuario("juan", "pass", "Juan");
        usuario.setId(2L);
        when(usuarioRepositorio.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(funcionarioRepositorio.buscarPorUsuario(2L)).thenReturn(Optional.of(new Funcionario()));

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
    }

    @Test
    void editar_cambiaDatos() {
        EditarFuncionarioComando comando = new EditarFuncionarioComando(1L, 2L, 3L, "Pedro Lopez", "Supervisor", true);
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        when(funcionarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(funcionario));

        Empresa empresa = new Empresa("Acme2", "Guayaquil");
        empresa.setId(3L);
        when(empresaRepositorio.buscarPorId(3L)).thenReturn(Optional.of(empresa));

        Usuario usuario = new Usuario("user", "pass", "Usuario");
        usuario.setId(2L);
        when(usuarioRepositorio.buscarPorId(2L)).thenReturn(Optional.of(usuario));
        when(funcionarioRepositorio.buscarPorUsuario(2L)).thenReturn(Optional.empty());

        Funcionario guardado = new Funcionario(null, empresa, "Pedro Lopez", "Supervisor");
        guardado.setId(1L);
        guardado.setActivo(true);
        when(funcionarioRepositorio.guardar(any(Funcionario.class))).thenReturn(guardado);

        Funcionario resultado = servicio.editar(comando);

        assertEquals("Pedro Lopez", resultado.getNombreCompleto());
        assertEquals("Supervisor", resultado.getCargo());
        assertTrue(resultado.isActivo());
    }

    @Test
    void eliminar_existente_elimina() {
        Funcionario funcionario = new Funcionario();
        funcionario.setId(1L);
        when(funcionarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(funcionario));

        servicio.eliminar(1L);

        verify(funcionarioRepositorio).eliminarPorId(1L);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(funcionarioRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.obtenerPorId(999L));
    }
}
