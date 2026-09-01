package com.acme.sica.aplicacion.permiso;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
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
class AuditoriaPermisoDecoradorTest {

    @Mock
    private GestionarPermisoCasoUso decorado;

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private AuditoriaPermisoDecorador decorador;

    @BeforeEach
    void setUp() {
        decorador = new AuditoriaPermisoDecorador(decorado, bitacoraRepositorio);
        SesionContexto.iniciar(new Sesion(1L, "admin", "Admin", Set.of("gestionar_permisos"), 30));
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void crear_delegaYRegistraAuditoria() {
        CrearPermisoComando comando = new CrearPermisoComando("P1", "Desc");
        Permiso permiso = new Permiso("P1", "Desc");
        permiso.setId(1L);
        when(decorado.crear(comando)).thenReturn(permiso);

        Permiso resultado = decorador.crear(comando);

        assertEquals(permiso, resultado);
        verify(decorado).crear(comando);
        verify(bitacoraRepositorio).guardar(any());
    }

    @Test
    void editar_delegaYRegistraAuditoria() {
        EditarPermisoComando comando = new EditarPermisoComando(1L, "P1", "Desc");
        Permiso permiso = new Permiso("P1", "Desc");
        permiso.setId(1L);
        when(decorado.editar(comando)).thenReturn(permiso);

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
