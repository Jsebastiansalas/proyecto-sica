package com.acme.sica.aplicacion.rol;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.RolEnUsoExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarRolServicioTest {

    @Mock
    private RolRepositorioPuerto rolRepositorio;

    @Mock
    private UsuarioRepositorioPuerto usuarioRepositorio;

    private ManejadorAutorizacion cadenaAutorizacion;

    private GestionarRolServicio servicio;

    @BeforeEach
    void setUp() {
        cadenaAutorizacion = new ManejadorAutorizacion() {
            @Override
            public void verificar(String permiso, String accion) {
                // No-op para tests
            }
        };
        servicio = new GestionarRolServicio(rolRepositorio, usuarioRepositorio, cadenaAutorizacion);
    }

    @Test
    void crear_nombreUnico_creaRol() {
        // Given
        CrearRolComando comando = new CrearRolComando("NUEVO_ROL", "Descripción");
        when(rolRepositorio.existePorNombre("NUEVO_ROL")).thenReturn(false);

        Rol rolGuardado = new Rol("NUEVO_ROL", "Descripción");
        rolGuardado.setId(1L);
        when(rolRepositorio.guardar(any(Rol.class))).thenReturn(rolGuardado);

        // When
        Rol resultado = servicio.crear(comando);

        // Then
        assertEquals("NUEVO_ROL", resultado.getNombre());
        assertEquals("Descripción", resultado.getDescripcion());
        assertEquals(1L, resultado.getId());
        verify(rolRepositorio).guardar(any(Rol.class));
    }

    @Test
    void crear_nombreDuplicado_lanzaExcepcion() {
        // Given
        CrearRolComando comando = new CrearRolComando("EXISTENTE", "Descripción");
        when(rolRepositorio.existePorNombre("EXISTENTE")).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.crear(comando));
        verify(rolRepositorio, never()).guardar(any());
    }

    @Test
    void editar_cambiaNombreYDescripcion() {
        // Given
        EditarRolComando comando = new EditarRolComando(1L, "NUEVO_NOMBRE", "Nueva descripción");
        Rol rolExistente = new Rol("ANTIGUO", "Descripción antigua");
        rolExistente.setId(1L);
        when(rolRepositorio.buscarPorId(1L)).thenReturn(Optional.of(rolExistente));
        when(rolRepositorio.existePorNombre("NUEVO_NOMBRE")).thenReturn(false);

        Rol rolGuardado = new Rol("NUEVO_NOMBRE", "Nueva descripción");
        rolGuardado.setId(1L);
        when(rolRepositorio.guardar(any(Rol.class))).thenReturn(rolGuardado);

        // When
        Rol resultado = servicio.editar(comando);

        // Then
        assertEquals("NUEVO_NOMBRE", resultado.getNombre());
        assertEquals("Nueva descripción", resultado.getDescripcion());
    }

    @Test
    void editar_mismoNombre_noVerificaDuplicado() {
        // Given
        EditarRolComando comando = new EditarRolComando(1L, "MISMO_NOMBRE", "Nueva descripción");
        Rol rolExistente = new Rol("MISMO_NOMBRE", "Descripción antigua");
        rolExistente.setId(1L);
        when(rolRepositorio.buscarPorId(1L)).thenReturn(Optional.of(rolExistente));

        Rol rolGuardado = new Rol("MISMO_NOMBRE", "Nueva descripción");
        rolGuardado.setId(1L);
        when(rolRepositorio.guardar(any(Rol.class))).thenReturn(rolGuardado);

        // When
        Rol resultado = servicio.editar(comando);

        // Then
        assertEquals("MISMO_NOMBRE", resultado.getNombre());
        verify(rolRepositorio, never()).existePorNombre(anyString());
    }

    @Test
    void editar_rolNoExiste_lanzaExcepcion() {
        // Given
        EditarRolComando comando = new EditarRolComando(999L, "NOMBRE", "Desc");
        when(rolRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.editar(comando));
    }

    @Test
    void eliminar_rolNoEnUso_eliminaCorrectamente() {
        // Given
        Long rolId = 1L;
        Rol rol = new Rol("TEST", "Test");
        rol.setId(rolId);
        when(rolRepositorio.buscarPorId(rolId)).thenReturn(Optional.of(rol));
        when(rolRepositorio.contarUsuariosConRol(rolId)).thenReturn(0L);

        // When
        servicio.eliminar(rolId);

        // Then
        verify(rolRepositorio).eliminarPorId(rolId);
    }

    @Test
    void eliminar_rolEnUso_lanzaExcepcion() {
        // Given
        Long rolId = 1L;
        Rol rol = new Rol("TEST", "Test");
        rol.setId(rolId);
        when(rolRepositorio.buscarPorId(rolId)).thenReturn(Optional.of(rol));
        when(rolRepositorio.contarUsuariosConRol(rolId)).thenReturn(5L);

        // When & Then
        assertThrows(RolEnUsoExcepcion.class, () -> servicio.eliminar(rolId));
        verify(rolRepositorio, never()).eliminarPorId(anyLong());
    }

    @Test
    void listarTodos_retornaLista() {
        // Given
        List<Rol> roles = List.of(new Rol("ROL1", "Desc1"), new Rol("ROL2", "Desc2"));
        when(rolRepositorio.listarTodos()).thenReturn(roles);

        // When
        List<Rol> resultado = servicio.listarTodos();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaRol() {
        // Given
        Rol rol = new Rol("TEST", "Test");
        rol.setId(1L);
        when(rolRepositorio.buscarPorId(1L)).thenReturn(Optional.of(rol));

        // When
        Rol resultado = servicio.obtenerPorId(1L);

        // Then
        assertEquals(rol, resultado);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        // Given
        when(rolRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.obtenerPorId(999L));
    }

    @Test
    void asignarRoles_listaVacia_lanzaExcepcion() {
        // Given
        AsignarRolesUsuarioComando comando = new AsignarRolesUsuarioComando(1L, Collections.emptySet());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.asignarRoles(comando));
    }

    @Test
    void asignarRoles_usuarioNoExiste_lanzaExcepcion() {
        // Given
        AsignarRolesUsuarioComando comando = new AsignarRolesUsuarioComando(1L, Set.of(1L));
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.asignarRoles(comando));
    }

    @Test
    void asignarRoles_rolNoExiste_lanzaExcepcion() {
        // Given
        AsignarRolesUsuarioComando comando = new AsignarRolesUsuarioComando(1L, Set.of(999L));
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(rolRepositorio.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> servicio.asignarRoles(comando));
    }

    @Test
    void asignarRoles_rolInactivo_lanzaExcepcion() {
        // Given
        AsignarRolesUsuarioComando comando = new AsignarRolesUsuarioComando(1L, Set.of(1L));
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        
        Rol rolInactivo = new Rol("INACTIVO", "Inactivo");
        rolInactivo.setId(1L);
        rolInactivo.setActivo(false);
        when(rolRepositorio.buscarPorId(1L)).thenReturn(Optional.of(rolInactivo));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> servicio.asignarRoles(comando));
    }

    @Test
    void asignarRoles_exitoso_asignaRoles() {
        // Given
        AsignarRolesUsuarioComando comando = new AsignarRolesUsuarioComando(1L, Set.of(1L, 2L));
        when(usuarioRepositorio.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        
        Rol rol1 = new Rol("ROL1", "Desc1");
        rol1.setId(1L);
        rol1.setActivo(true);
        
        Rol rol2 = new Rol("ROL2", "Desc2");
        rol2.setId(2L);
        rol2.setActivo(true);
        
        when(rolRepositorio.buscarPorId(1L)).thenReturn(Optional.of(rol1));
        when(rolRepositorio.buscarPorId(2L)).thenReturn(Optional.of(rol2));

        // When
        servicio.asignarRoles(comando);

        // Then
        verify(usuarioRepositorio).asignarRoles(eq(1L), eq(Set.of(1L, 2L)));
    }
}