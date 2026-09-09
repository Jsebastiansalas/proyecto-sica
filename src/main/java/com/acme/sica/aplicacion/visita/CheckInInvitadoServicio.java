package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PersonaBloqueadaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.entrada.CheckInInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VehiculoRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.dominio.modelo.Vehiculo;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de aplicación para el check-in de invitados (HU-12).
 *
 * Si la persona tiene una visita abierta (DENTRO), la cierra
 * automáticamente como CERRADA_POR_SISTEMA antes de crear la nueva visita.
 */
public class CheckInInvitadoServicio implements CheckInInvitadoCasoUso {

    private static final String PERMISO_REQUERIDO = "check_in_invitado";
    private static final String MOTIVO_CIERRE_AUTOMATICO = "Cierre automático: persona realiza nuevo check-in";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final VehiculoRepositorioPuerto vehiculoRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;
    private final EstrategiaCierreSistema estrategiaCierreSistema;

    public CheckInInvitadoServicio(VisitaRepositorioPuerto visitaRepositorio,
                                   PersonaRepositorioPuerto personaRepositorio,
                                   ManejadorAutorizacion cadenaAutorizacion,
                                   EstrategiaCierreSistema estrategiaCierreSistema) {
        this(visitaRepositorio, personaRepositorio, null, cadenaAutorizacion, estrategiaCierreSistema);
    }

    public CheckInInvitadoServicio(VisitaRepositorioPuerto visitaRepositorio,
                                   PersonaRepositorioPuerto personaRepositorio,
                                   VehiculoRepositorioPuerto vehiculoRepositorio,
                                   ManejadorAutorizacion cadenaAutorizacion,
                                   EstrategiaCierreSistema estrategiaCierreSistema) {
        this.visitaRepositorio = visitaRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.vehiculoRepositorio = vehiculoRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
        this.estrategiaCierreSistema = estrategiaCierreSistema;
    }

    /**
     * Registra el ingreso de un invitado.
     */
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
            throw new PersonaBloqueadaExcepcion("La persona '" + persona.getNombreCompleto()
                    + "' está bloqueada y no puede ingresar");
        }

        Optional<Visita> visitaAbierta = visitaRepositorio.buscarVisitaAbiertaPorPersona(persona.getId());
        if (visitaAbierta.isPresent()) {
            estrategiaCierreSistema.regularizar(visitaAbierta.get(), MOTIVO_CIERRE_AUTOMATICO);
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
        
        if (comando.getPlacaVehiculo() != null && !comando.getPlacaVehiculo().isBlank()) {
            Vehiculo vehiculo = vehiculoRepositorio.buscarPorPlaca(comando.getPlacaVehiculo().trim())
                    .orElseGet(() -> {
                        Vehiculo nuevo = new Vehiculo(
                                comando.getPlacaVehiculo().trim(),
                                comando.getMarcaVehiculo(),
                                comando.getTipoVehiculo()
                        );
                        return vehiculoRepositorio.guardar(nuevo);
                    });
            visita.setVehiculo(vehiculo);
        }

        if (comando.getActivos() != null && !comando.getActivos().isEmpty()) {
            visita.setActivos(comando.getActivos());
        }

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
