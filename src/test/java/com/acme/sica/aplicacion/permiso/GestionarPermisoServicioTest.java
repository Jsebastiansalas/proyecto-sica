package com.acme.sica.aplicacion.permiso;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.puerto.salida.PermisoRepositorioPuerto;
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
class GestionarPermisoServicioTest {

    @Mock
    private PermisoRepositorioPuerto permisoRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;
    private GestionarPermisoServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {}
        };
        servicio = new GestionarPermisoServicio(permisoRepositorio, cadenaAutorizacion);
    }

    @Test
    void crear_codigoUnico_creaPermiso() {
        CrearPermisoComando comando = new CrearPermisoComando("PERMISO_1", "Descripción");
        when(permisoRepositorio.existePorCodigo("PERMISO_1")).thenReturn(false);

        Permiso guardado = new Permiso("PERMISO_1", "Descripción");
        guardado.setId(1L);
        when(permisoRepositorio.guardar(any(Permiso.class))).thenReturn(guardado);

        Permiso resultado = servicio.crear(comando);

        assertEquals("PERMISO_1", resultado.getNombre());
        assertEquals("Descripción", resultado.getDescripcion());
    }

    @Test
    void crear_codigoDuplicado_lanzaExcepcion() {
        CrearPermisoComando comando = new CrearPermisoComando("DUPLICADO", "Desc");
        when(permisoRepositorio.existePorCodigo("DUPLICADO")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
    }

    @Test
    void editar_cambiaDatos() {
        EditarPermisoComando comando = new EditarPermisoComando(1L, "NUEVO", "Nueva desc");
        Permiso permiso = new Permiso("ANTIGUO", "Desc");
        permiso.setId(1L);
        when(permisoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(permiso));
        when(permisoRepositorio.existePorCodigo("NUEVO")).thenReturn(false);

        Permiso guardado = new Permiso("NUEVO", "Nueva desc");
        guardado.setId(1L);
        when(permisoRepositorio.guardar(any(Permiso.class))).thenReturn(guardado);

        Permiso resultado = servicio.editar(comando);

        assertEquals("NUEVO", resultado.getNombre());
    }

    @Test
    void eliminar_existente_elimina() {
        Permiso permiso = new Permiso("P", "D");
        permiso.setId(1L);
        when(permisoRepositorio.buscarPorId(1L)).thenReturn(Optional.of(permiso));

        servicio.eliminar(1L);

        verify(permisoRepositorio).eliminarPorId(1L);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(permisoRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.obtenerPorId(999L));
    }
}
