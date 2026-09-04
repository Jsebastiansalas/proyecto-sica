package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PersonaBloqueadaExcepcion;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.entrada.RegistrarNoAnunciadoCasoUso;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio de aplicación para registrar invitados no anunciados (HU-13).
 *
 * Si la persona no existe la crea como INVITADO. Crea una visita en estado
 * PENDIENTE_APROBACION_OLVIDO para que un funcionario la apruebe.
 */
public class RegistrarNoAnunciadoServicio implements RegistrarNoAnunciadoCasoUso {

    private static final String PERMISO_REQUERIDO = "registrar_no_anunciado";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final FuncionarioRepositorioPuerto funcionarioRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public RegistrarNoAnunciadoServicio(VisitaRepositorioPuerto visitaRepositorio,
                                        PersonaRepositorioPuerto personaRepositorio,
                                        FuncionarioRepositorioPuerto funcionarioRepositorio,
                                        ManejadorAutorizacion cadenaAutorizacion) {
        this.visitaRepositorio = visitaRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.funcionarioRepositorio = funcionarioRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Registra la información recibida en el sistema.
     */
    @Override
    public Visita registrar(RegistrarNoAnunciadoComando comando) {
        autorizar("registrar no anunciado");

        if (comando.getDocumento() == null || comando.getDocumento().isBlank()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (comando.getNombreCompleto() == null || comando.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (comando.getFuncionarioId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un funcionario responsable");
        }

        String documento = comando.getDocumento().trim();
        Persona persona = personaRepositorio.buscarPorDocumento(documento)
                .orElseGet(() -> crearPersona(documento, comando));

        if (persona.isBloqueada()) {
            throw new PersonaBloqueadaExcepcion("La persona '" + persona.getNombreCompleto()
                    + "' está bloqueada y no puede ser registrada");
        }

        Funcionario funcionario = funcionarioRepositorio.buscarPorId(comando.getFuncionarioId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Funcionario no encontrado con id " + comando.getFuncionarioId()));
        if (!funcionario.isActivo()) {
            throw new IllegalArgumentException("El funcionario responsable no está activo");
        }

        Visita visita = new Visita(persona, null, funcionario,
                comando.getMotivo(), LocalDateTime.now(), null);
        visita.setEstado(EstadoVisita.PENDIENTE_APROBACION);
        return visitaRepositorio.guardar(visita);
    }

    private Persona crearPersona(String documento, RegistrarNoAnunciadoComando comando) {
        Persona nueva = new Persona(TipoPersona.INVITADO, documento,
                comando.getNombreCompleto().trim(), comando.getFotoUrl());
        return personaRepositorio.guardar(nueva);
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
