package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PersonaBloqueadaExcepcion;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.entrada.RegistrarTrabajadorCasoUso;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.time.LocalDateTime;

/**
 * Servicio de aplicación para registrar el ingreso de un trabajador (HU-15).
 *
 * Los trabajadores no necesitan aprobación previa: ingresan directamente.
 * Si la persona no existe la crea como TRABAJADOR. La visita se crea
 * directamente en estado DENTRO con fechaHoraIngreso=now.
 */
public class RegistrarTrabajadorServicio implements RegistrarTrabajadorCasoUso {

    private static final String PERMISO_REQUERIDO = "registrar_trabajador";

    private final VisitaRepositorioPuerto visitaRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final FuncionarioRepositorioPuerto funcionarioRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public RegistrarTrabajadorServicio(VisitaRepositorioPuerto visitaRepositorio,
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
    public Visita registrar(RegistrarTrabajadorComando comando) {
        autorizar("registrar trabajador");

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
        Persona persona = personaRepositorio.buscarPorDocumento(comando.getDocumento().trim())
                .orElseGet(() -> crearTrabajador(documento, comando));

        if (persona.isBloqueada()) {
            throw new PersonaBloqueadaExcepcion("La persona '" + persona.getNombreCompleto()
                    + "' está bloqueada y no puede ingresar");
        }

        Funcionario funcionario = funcionarioRepositorio.buscarPorId(comando.getFuncionarioId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Funcionario no encontrado con id " + comando.getFuncionarioId()));
        if (!funcionario.isActivo()) {
            throw new IllegalArgumentException("El funcionario responsable no está activo");
        }

        Visita visita = new Visita(persona, null, null,
                "Ingreso de trabajador (carnet olvidado)", LocalDateTime.now(), null);
        visita.setEstado(EstadoVisita.PENDIENTE_APROBACION_OLVIDO);
        visita.setFechaHoraEsperada(LocalDateTime.now());
        return visitaRepositorio.guardar(visita);
    }

    private Persona crearTrabajador(String documento, RegistrarTrabajadorComando comando) {
        Persona nueva = new Persona(TipoPersona.TRABAJADOR, documento,
                comando.getNombreCompleto().trim(), comando.getFotoUrl());
        return personaRepositorio.guardar(nueva);
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
