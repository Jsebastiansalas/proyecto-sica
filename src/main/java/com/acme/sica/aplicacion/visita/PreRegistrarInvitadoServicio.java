package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.entrada.PreRegistrarInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.time.LocalDateTime;

/**
 * Servicio de aplicación para pre-registrar invitados (HU-11).
 *
 * Valida permisos, tipo de persona, bloqueo, funcionario activo y fecha futura.
 * La auditoría la realiza un decorador (HU-06).
 */
public class PreRegistrarInvitadoServicio implements PreRegistrarInvitadoCasoUso {

    private static final String PERMISO_REQUERIDO = "pre_registrar_invitado";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final FuncionarioRepositorioPuerto funcionarioRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public PreRegistrarInvitadoServicio(VisitaRepositorioPuerto visitaRepositorio,
                                        PersonaRepositorioPuerto personaRepositorio,
                                        FuncionarioRepositorioPuerto funcionarioRepositorio,
                                        ManejadorAutorizacion cadenaAutorizacion) {
        this.visitaRepositorio = visitaRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public Visita preRegistrar(PreRegistrarInvitadoComando comando) {
        autorizar("pre registrar invitado");

        if (comando.getPersonaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una persona");
        }
        if (comando.getFuncionarioId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un funcionario responsable");
        }
        if (comando.getFechaHoraEsperada() == null) {
            throw new IllegalArgumentException("Debe indicar la fecha y hora esperada de la visita");
        }
        if (comando.getFechaHoraEsperada().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha y hora esperada debe ser futura");
        }

        Persona persona = personaRepositorio.buscarPorId(comando.getPersonaId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Persona no encontrada con id " + comando.getPersonaId()));

        if (persona.getTipo() != TipoPersona.INVITADO) {
            throw new IllegalArgumentException("Solo se pueden pre-registrar invitados");
        }
        if (persona.isBloqueada()) {
            throw new IllegalArgumentException("La persona está bloqueada y no puede ser registrada");
        }

        Funcionario funcionario = funcionarioRepositorio.buscarPorId(comando.getFuncionarioId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Funcionario no encontrado con id " + comando.getFuncionarioId()));
        if (!funcionario.isActivo()) {
            throw new IllegalArgumentException("El funcionario responsable no está activo");
        }

        Visita visita = new Visita(persona, null, funcionario,
                comando.getMotivo(), comando.getFechaHoraEsperada(), null);
        visita.setEstado(EstadoVisita.APROBADO);
        return visitaRepositorio.guardar(visita);
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
