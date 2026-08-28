package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.entrada.CheckInInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación para el check-in de invitados (HU-12).
 *
 * Valida que la persona exista, no esté bloqueada y tenga una visita
 * aprobada pendiente de ingreso. Marca el ingreso (estado DENTRO).
 */
public class CheckInInvitadoServicio implements CheckInInvitadoCasoUso {

    private static final String PERMISO_REQUERIDO = "check_in_invitado";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public CheckInInvitadoServicio(VisitaRepositorioPuerto visitaRepositorio,
                                   PersonaRepositorioPuerto personaRepositorio,
                                   ManejadorAutorizacion cadenaAutorizacion) {
        this.visitaRepositorio = visitaRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Visita checkIn(CheckInInvitadoComando comando) {
        autorizar("check-in invitado");

        if (comando.getDocumento() == null || comando.getDocumento().isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }

        Persona persona = personaRepositorio.buscarPorDocumento(comando.getDocumento().trim())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "No existe una persona con el documento '" + comando.getDocumento().trim() + "'"));

        if (persona.isBloqueada()) {
            throw new IllegalArgumentException("La persona '" + persona.getNombreCompleto()
                    + "' está bloqueada y no puede ingresar");
        }

        List<Visita> aprobadas = visitaRepositorio.buscarPorPersonaYEstado(
                persona.getId(), EstadoVisita.APROBADO);

        if (aprobadas.isEmpty()) {
            throw new IllegalStateException("No hay una visita aprobada pendiente de ingreso para '"
                    + persona.getNombreCompleto() + "'");
        }

        Visita visita = aprobadas.get(0);
        visita.setFechaHoraIngreso(LocalDateTime.now());
        visita.setEstado(EstadoVisita.DENTRO);
        return visitaRepositorio.guardar(visita);
    }

    public List<Visita> listarAprobadas() {
        autorizar("listar visitas aprobadas");
        return visitaRepositorio.listarTodos().stream()
                .filter(v -> v.getEstado() == EstadoVisita.APROBADO)
                .toList();
    }

    public List<Visita> listarDentro() {
        autorizar("listar visitas dentro");
        return visitaRepositorio.listarTodos().stream()
                .filter(v -> v.getEstado() == EstadoVisita.DENTRO)
                .toList();
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
