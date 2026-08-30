package com.acme.sica.infraestructura.seguridad.autorizacion;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FabricaCadenaAutorizacionTest {

    @Mock
    private BitacoraRepositorioPuerto bitacoraRepositorio;

    private ManejadorAutorizacion cadena;

    @BeforeEach
    void setUp() {
        cadena = FabricaCadenaAutorizacion.crear(bitacoraRepositorio);
        SesionContexto.cerrar();
    }

    @AfterEach
    void tearDown() {
        SesionContexto.cerrar();
    }

    @Test
    void verificar_sinSesionActiva_lanzaExcepcion() {
        // Given: no hay sesión activa
        // When & Then
        assertThrows(PermisoDenegadoExcepcion.class, () ->
                cadena.verificar("gestionar_roles", "crear rol"));
    }

    @Test
    void verificar_conSesionActivaPeroSinPermiso_lanzaExcepcionYRegistraAuditoria() {
        // Given: sesión activa SIN el permiso requerido
        Sesion sesion = new Sesion(1L, "usuario", "Usuario Test", Set.of("login"));
        SesionContexto.iniciar(sesion);

        // When & Then
        PermisoDenegadoExcepcion ex = assertThrows(PermisoDenegadoExcepcion.class, () ->
                cadena.verificar("gestionar_roles", "crear rol"));

        assertEquals("No tiene permiso ('gestionar_roles') para crear rol", ex.getMessage());

        // Verificar que se registró en bitácora
        ArgumentCaptor<BitacoraAuditoria> captor = ArgumentCaptor.forClass(BitacoraAuditoria.class);
        verify(bitacoraRepositorio).guardar(captor.capture());

        BitacoraAuditoria registro = captor.getValue();
        assertEquals("1", String.valueOf(registro.getUsuarioId()));
        assertEquals("usuario", registro.getUsuarioNombre());
        assertEquals(TipoAccionAuditoria.ACCESO_DENEGADO.name(), registro.getAccion());
        assertEquals("AUTORIZACION", registro.getEntidad());
        assertTrue(registro.getDetalles().contains("gestionar_roles"));
    }

    @Test
    void verificar_conSesionActivaYPermisoCorrecto_noLanzaExcepcion() {
        // Given: sesión activa CON el permiso requerido
        Sesion sesion = new Sesion(1L, "usuario", "Usuario Test", Set.of("login", "gestionar_roles"));
        SesionContexto.iniciar(sesion);

        // When & Then
        assertDoesNotThrow(() -> cadena.verificar("gestionar_roles", "crear rol"));

        // No se debe registrar auditoría de acceso denegado
        verify(bitacoraRepositorio, never()).guardar(any());
    }

    @Test
    void verificar_conSesionAdmin_conTodosPermisos_noLanzaExcepcion() {
        // Given: sesión de admin con muchos permisos
        Sesion sesion = new Sesion(1L, "admin", "Admin", Set.of(
                "login", "gestionar_roles", "gestionar_permisos", "gestionar_usuarios",
                "gestionar_empresas", "gestionar_funcionarios", "registrar_persona",
                "pre_registrar_invitado", "check_in_invitado", "registrar_no_anunciado",
                "aprobar_rechazar", "registrar_trabajador", "regularizar_salida",
                "check_out", "registrar_incidente", "bloquear_persona",
                "generar_reporte_accesos", "generar_reporte_incidentes",
                "consultar_bitacora", "asignar_roles"
        ));
        SesionContexto.iniciar(sesion);

        // When & Then - todos los permisos deben pasar
        assertDoesNotThrow(() -> cadena.verificar("gestionar_roles", "crear rol"));
        assertDoesNotThrow(() -> cadena.verificar("gestionar_usuarios", "crear usuario"));
        assertDoesNotThrow(() -> cadena.verificar("check_in_invitado", "check-in invitado"));
        assertDoesNotThrow(() -> cadena.verificar("bloquear_persona", "bloquear persona"));
    }
}