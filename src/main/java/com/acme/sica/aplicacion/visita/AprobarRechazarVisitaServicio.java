package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.entrada.AprobarRechazarVisitaCasoUso;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;
import java.util.stream.Stream;

/**
 * Servicio de aplicación para aprobar o rechazar visitas (HU-14).
 *
 * Lista las visitas en PENDIENTE_APROBACION o PENDIENTE_APROBACION_OLVIDO,
 * y permite aprobarlas (→ APROBADO) o rechazarlas (→ RECHAZADO).
 */
public class AprobarRechazarVisitaServicio implements AprobarRechazarVisitaCasoUso {

    private static final String PERMISO_REQUERIDO = "aprobar_rechazar";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public AprobarRechazarVisitaServicio(VisitaRepositorioPuerto visitaRepositorio,
                                         ManejadorAutorizacion cadenaAutorizacion) {
        this.visitaRepositorio = visitaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Aprueba la visita identificada.
     */
    @Override
    public Visita aprobar(Long visitaId) {
        autorizar("aprobar visita");

        Visita visita = buscarVisita(visitaId);
        verificarEstadoPendiente(visita);

        visita.setEstado(EstadoVisita.APROBADO);
        return visitaRepositorio.guardar(visita);
    }

    /**
     * Rechaza la visita identificada.
     */
    @Override
    public Visita rechazar(RechazarVisitaComando comando) {
        autorizar("rechazar visita");

        Visita visita = buscarVisita(comando.getVisitaId());
        verificarEstadoPendiente(visita);

        visita.setEstado(EstadoVisita.RECHAZADO);
        if (comando.getMotivo() != null && !comando.getMotivo().isBlank()) {
            String motivoActual = visita.getMotivo();
            visita.setMotivo((motivoActual != null ? motivoActual + " | " : "") + "RECHAZADO: " + comando.getMotivo());
        }
        return visitaRepositorio.guardar(visita);
    }

    /**
     * Obtiene el listado de entidades pendientes.
     */
    @Override
    public List<Visita> listarPendientes() {
        autorizar("listar visitas pendientes");
        return Stream.concat(
                visitaRepositorio.listarTodos().stream()
                        .filter(v -> v.getEstado() == EstadoVisita.PENDIENTE_APROBACION),
                visitaRepositorio.listarTodos().stream()
                        .filter(v -> v.getEstado() == EstadoVisita.PENDIENTE_APROBACION_OLVIDO)
        ).toList();
    }

    private Visita buscarVisita(Long visitaId) {
        return visitaRepositorio.buscarPorId(visitaId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Visita no encontrada con id " + visitaId));
    }

    private void verificarEstadoPendiente(Visita visita) {
        EstadoVisita estado = visita.getEstado();
        if (estado != EstadoVisita.PENDIENTE_APROBACION
                && estado != EstadoVisita.PENDIENTE_APROBACION_OLVIDO) {
            throw new IllegalStateException(
                    "La visita no está pendiente de aprobación (estado actual: " + estado + ")");
        }
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
