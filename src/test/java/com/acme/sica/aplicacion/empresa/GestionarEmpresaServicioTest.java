package com.acme.sica.aplicacion.empresa;

import com.acme.sica.dominio.excepciones.EmpresaEnUsoExcepcion;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.puerto.salida.EmpresaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarEmpresaServicioTest {

    @Mock
    private EmpresaRepositorioPuerto empresaRepositorio;

    @Mock
    private FuncionarioRepositorioPuerto funcionarioRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private GestionarEmpresaServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new GestionarEmpresaServicio(empresaRepositorio, funcionarioRepositorio, cadenaAutorizacion);
    }

    @Test
    void crear_nombreUnico_creaEmpresa() {
        CrearEmpresaComando comando = new CrearEmpresaComando("Acme", "Quito");
        when(empresaRepositorio.existePorNombre("Acme")).thenReturn(false);

        Empresa empresaGuardada = new Empresa("Acme", "Quito");
        empresaGuardada.setId(1L);
        when(empresaRepositorio.guardar(any(Empresa.class))).thenReturn(empresaGuardada);

        Empresa resultado = servicio.crear(comando);

        assertEquals("Acme", resultado.getNombre());
        assertEquals("Quito", resultado.getUbicacion());
        assertEquals(1L, resultado.getId());
        verify(empresaRepositorio).guardar(any(Empresa.class));
    }

    @Test
    void crear_nombreDuplicado_lanzaExcepcion() {
        CrearEmpresaComando comando = new CrearEmpresaComando("Acme", "Quito");
        when(empresaRepositorio.existePorNombre("Acme")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
        verify(empresaRepositorio, never()).guardar(any());
    }

    @Test
    void editar_cambiaNombreYUbicacion() {
        EditarEmpresaComando comando = new EditarEmpresaComando(1L, "Acme Nuevo", "Guayaquil", true);
        Empresa empresaExistente = new Empresa("Acme", "Quito");
        empresaExistente.setId(1L);
        when(empresaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(empresaExistente));
        when(empresaRepositorio.existePorNombre("Acme Nuevo")).thenReturn(false);

        Empresa empresaGuardada = new Empresa("Acme Nuevo", "Guayaquil");
        empresaGuardada.setId(1L);
        when(empresaRepositorio.guardar(any(Empresa.class))).thenReturn(empresaGuardada);

        Empresa resultado = servicio.editar(comando);

        assertEquals("Acme Nuevo", resultado.getNombre());
        assertEquals("Guayaquil", resultado.getUbicacion());
    }

    @Test
    void editar_mismoNombre_noVerificaDuplicado() {
        EditarEmpresaComando comando = new EditarEmpresaComando(1L, "Acme", "Guayaquil", true);
        Empresa empresaExistente = new Empresa("Acme", "Quito");
        empresaExistente.setId(1L);
        when(empresaRepositorio.buscarPorId(1L)).thenReturn(Optional.of(empresaExistente));

        Empresa empresaGuardada = new Empresa("Acme", "Guayaquil");
        empresaGuardada.setId(1L);
        when(empresaRepositorio.guardar(any(Empresa.class))).thenReturn(empresaGuardada);

        servicio.editar(comando);

        verify(empresaRepositorio, never()).existePorNombre(anyString());
    }

    @Test
    void editar_empresaNoExiste_lanzaExcepcion() {
        EditarEmpresaComando comando = new EditarEmpresaComando(999L, "Acme", "Quito", true);
        when(empresaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.editar(comando));
    }

    @Test
    void eliminar_sinFuncionarios_eliminaCorrectamente() {
        Long empresaId = 1L;
        Empresa empresa = new Empresa("Acme", "Quito");
        empresa.setId(empresaId);
        when(empresaRepositorio.buscarPorId(empresaId)).thenReturn(Optional.of(empresa));
        when(funcionarioRepositorio.buscarPorEmpresa(empresaId)).thenReturn(List.of());

        servicio.eliminar(empresaId);

        verify(empresaRepositorio).eliminarPorId(empresaId);
    }

    @Test
    void eliminar_conFuncionarios_lanzaExcepcion() {
        Long empresaId = 1L;
        Empresa empresa = new Empresa("Acme", "Quito");
        empresa.setId(empresaId);
        when(empresaRepositorio.buscarPorId(empresaId)).thenReturn(Optional.of(empresa));
        when(funcionarioRepositorio.buscarPorEmpresa(empresaId)).thenReturn(List.of(new Funcionario()));

        assertThrows(EmpresaEnUsoExcepcion.class, () -> servicio.eliminar(empresaId));
        verify(empresaRepositorio, never()).eliminarPorId(anyLong());
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(empresaRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.obtenerPorId(999L));
    }
}
