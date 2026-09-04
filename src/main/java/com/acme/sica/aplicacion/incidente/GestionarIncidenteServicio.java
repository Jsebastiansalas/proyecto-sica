package com.acme.sica.aplicacion.incidente;

import com.acme.sica.aplicacion.autenticacion.Sesion;
import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.entrada.GestionarIncidenteCasoUso;
import com.acme.sica.dominio.puerto.salida.IncidenteRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación encargado de gestionar incidentes.
 * Contiene la lógica de negocio y coordina los puertos de entrada y salida.
 */
public class GestionarIncidenteServicio implements GestionarIncidenteCasoUso {

    private static final String PERMISO_REQUERIDO = "registrar_incidente";

    private final IncidenteRepositorioPuerto incidenteRepositorio;
    private final PersonaRepositorioPuerto personaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public GestionarIncidenteServicio(IncidenteRepositorioPuerto incidenteRepositorio,
                                      PersonaRepositorioPuerto personaRepositorio,
                                      ManejadorAutorizacion cadenaAutorizacion) {
        this.incidenteRepositorio = incidenteRepositorio;
        this.personaRepositorio = personaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Incidente crear(CrearIncidenteComando comando) {
        autorizar("registrar incidente");
        if (comando.getPersonaId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una persona");
        }
        if (comando.getDescripcion() == null || comando.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (comando.getGravedad() == null) {
            throw new IllegalArgumentException("La gravedad es obligatoria");
        }

        Persona persona = personaRepositorio.buscarPorId(comando.getPersonaId())
                .orElseThrow(() -> new EntidadNoEncontradaExcepcion(
                        "Persona no encontrada con id " + comando.getPersonaId()));
        Sesion sesion = SesionContexto.obtener()
                .orElseThrow(() -> new IllegalStateException("No hay una sesión activa"));
        Usuario registradoPor = new Usuario();
        registradoPor.setId(sesion.getUsuarioId());
        registradoPor.setUsername(sesion.getUsername());
        registradoPor.setNombreCompleto(sesion.getNombreCompleto());

        Incidente incidente = new Incidente(persona, null, comando.getDescripcion().trim(),
                comando.getGravedad(), registradoPor);
        return incidenteRepositorio.guardar(incidente);
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Incidente> listarTodos() {
        autorizar("listar incidentes");
        return incidenteRepositorio.listarTodos();
    }

    /**
     * Obtiene el listado de personas registradas.
     */
    @Override
    public List<Persona> listarPersonas() {
        autorizar("registrar incidente");
        return personaRepositorio.listarTodos();
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
