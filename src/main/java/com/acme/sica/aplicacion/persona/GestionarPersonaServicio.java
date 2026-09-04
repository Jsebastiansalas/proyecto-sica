package com.acme.sica.aplicacion.persona;

import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.puerto.entrada.GestionarPersonaCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación para la gestión de personas (HU-10).
 *
 * Se encarga de la lógica de negocio y de la autorización (Chain of
 * Responsibility). La auditoría la realiza un decorador (HU-06).
 */
public class GestionarPersonaServicio implements GestionarPersonaCasoUso {

    private static final String PERMISO_REQUERIDO = "registrar_persona";

    private final PersonaRepositorioPuerto personaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarPersonaServicio(PersonaRepositorioPuerto personaRepositorio,
                                    ManejadorAutorizacion cadenaAutorizacion) {
        this.personaRepositorio = personaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Persona crear(CrearPersonaComando comando) {
        autorizar("crear persona");

        if (comando.getDocumentoIdentidad() == null || comando.getDocumentoIdentidad().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio");
        }
        if (comando.getNombreCompleto() == null || comando.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        if (comando.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de persona es obligatorio");
        }
        verificarDocumentoDuplicado(comando.getDocumentoIdentidad());

        Persona persona = new Persona(comando.getTipo(), comando.getDocumentoIdentidad().trim(),
                comando.getNombreCompleto().trim(), comando.getFotoUrl());
        return personaRepositorio.guardar(persona);
    }

    /**
     * Actualiza una entidad existente con los datos del comando.
     */
    @Override
    public Persona editar(EditarPersonaComando comando) {
        autorizar("editar persona");

        Persona persona = personaRepositorio.buscarPorId(comando.getId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Persona no encontrada con id " + comando.getId()));

        if (comando.getDocumentoIdentidad() == null || comando.getDocumentoIdentidad().isBlank()) {
            throw new IllegalArgumentException("El documento de identidad es obligatorio");
        }
        if (comando.getNombreCompleto() == null || comando.getNombreCompleto().isBlank()) {
            throw new IllegalArgumentException("El nombre completo es obligatorio");
        }
        if (comando.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de persona es obligatorio");
        }

        if (!persona.getDocumentoIdentidad().equals(comando.getDocumentoIdentidad())) {
            verificarDocumentoDuplicado(comando.getDocumentoIdentidad());
        }

        persona.setDocumentoIdentidad(comando.getDocumentoIdentidad().trim());
        persona.setNombreCompleto(comando.getNombreCompleto().trim());
        persona.setFotoUrl(comando.getFotoUrl());
        persona.setTipo(comando.getTipo());
        return personaRepositorio.guardar(persona);
    }

    /**
     * Elimina la entidad identificada por el id proporcionado.
     */
    @Override
    public void eliminar(Long personaId) {
        autorizar("eliminar persona");

        Persona persona = personaRepositorio.buscarPorId(personaId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Persona no encontrada con id " + personaId));

        personaRepositorio.eliminarPorId(personaId);
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Persona> listarTodos() {
        autorizar("listar personas");
        return personaRepositorio.listarTodos();
    }

    /**
     * Recupera una entidad a partir de su identificador.
     */
    @Override
    public Persona obtenerPorId(Long personaId) {
        autorizar("obtener persona");
        return personaRepositorio.buscarPorId(personaId)
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Persona no encontrada con id " + personaId));
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }

    private void verificarDocumentoDuplicado(String documento) {
        if (personaRepositorio.existePorDocumento(documento.trim())) {
            throw new IllegalArgumentException("Ya existe una persona con el documento '" + documento.trim() + "'");
        }
    }
}
