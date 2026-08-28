package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.entrada.CheckOutCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación para el check-out de personas (HU-17).
 *
 * Busca la visita abierta (DENTRO) por documento, registra la salida
 * (fechaHoraSalida=now, estado=CERRADA).
 */
public class CheckOutServicio implements CheckOutCasoUso {

    private static final String PERMISO_REQUERIDO = "check_out";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public CheckOutServicio(VisitaRepositorioPuerto visitaRepositorio,
                            PersonaRepositorioPuerto personaRepositorio,
                            ManejadorAutorizacion cadenaAutorizacion) {
        this.visitaRepositorio = visitaRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Visita checkOut(CheckOutComando comando) {
        autorizar("check-out");

        if (comando.getDocumento() == null || comando.getDocumento().isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }

        Persona persona = personaRepositorio.buscarPorDocumento(comando.getDocumento().trim())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "No existe una persona con el documento '" + comando.getDocumento().trim() + "'"));

        Visita visita = visitaRepositorio.buscarVisitaAbiertaPorPersona(persona.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No hay una visita abierta para '" + persona.getNombreCompleto() + "'"));

        visita.setEstado(EstadoVisita.CERRADA);
        visita.setFechaHoraSalida(LocalDateTime.now());
        return visitaRepositorio.guardar(visita);
    }

    @Override
    public List<Visita> listarDentro() {
        autorizar("listar personas dentro");
        return visitaRepositorio.listarTodos().stream()
                .filter(v -> v.getEstado() == EstadoVisita.DENTRO)
                .toList();
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
