package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.entrada.RegularizarSalidaCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;
import java.util.Map;

/**
 * Servicio de aplicación para regularizar salidas olvidadas (HU-16).
 *
 * Utiliza el patrón Strategy: recibe un {@link TipoRegularizacion} en el
 * comando y delega a la estrategia correspondiente.
 */
public class RegularizarSalidaServicio implements RegularizarSalidaCasoUso {

    private static final String PERMISO_REQUERIDO = "regularizar_salida";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;
    private final Map<TipoRegularizacion, EstrategiaSalidaOlvidada> estrategias;

    public RegularizarSalidaServicio(VisitaRepositorioPuerto visitaRepositorio,
                                     PersonaRepositorioPuerto personaRepositorio,
                                     ManejadorAutorizacion cadenaAutorizacion,
                                     Map<TipoRegularizacion, EstrategiaSalidaOlvidada> estrategias) {
        this.visitaRepositorio = visitaRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
        this.estrategias = estrategias;
    }

    @Override
    public Visita regularizar(RegularizarSalidaComando comando) {
        autorizar("regularizar salida olvidada");

        if (comando.getDocumento() == null || comando.getDocumento().isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (comando.getTipo() == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de regularización");
        }

        Persona persona = personaRepositorio.buscarPorDocumento(comando.getDocumento().trim())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "No existe una persona con el documento '" + comando.getDocumento().trim() + "'"));

        Visita visita = visitaRepositorio.buscarVisitaAbiertaPorPersona(persona.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No hay una visita abierta para '" + persona.getNombreCompleto() + "'"));

        EstrategiaSalidaOlvidada estrategia = estrategias.get(comando.getTipo());
        if (estrategia == null) {
            throw new IllegalStateException("No hay estrategia para el tipo " + comando.getTipo());
        }

        return estrategia.regularizar(visita, comando.getMotivo());
    }

    @Override
    public List<Visita> listarSalidasOlvidadas() {
        autorizar("listar salidas olvidadas");
        return visitaRepositorio.listarTodos().stream()
                .filter(v -> v.getEstado() == EstadoVisita.DENTRO)
                .toList();
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
