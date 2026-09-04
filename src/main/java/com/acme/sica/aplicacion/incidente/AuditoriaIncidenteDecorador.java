package com.acme.sica.aplicacion.incidente;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarIncidenteCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría que envuelve el caso de uso de un incidente
 * y registra las acciones realizadas en la bitácora de auditoría.
 */
public class AuditoriaIncidenteDecorador implements GestionarIncidenteCasoUso {

    private final GestionarIncidenteCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaIncidenteDecorador(GestionarIncidenteCasoUso decorado,
                                       BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Crea una nueva entidad a partir del comando recibido.
     */
    @Override
    public Incidente crear(CrearIncidenteComando comando) {
        Incidente incidente = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_INCIDENTE,
                "INCIDENTE", incidente.getId(), "Incidente registrado: " + incidente.getDescripcion());
        return incidente;
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<Incidente> listarTodos() {
        return decorado.listarTodos();
    }

    /**
     * Obtiene el listado de personas registradas.
     */
    @Override
    public List<com.acme.sica.dominio.modelo.Persona> listarPersonas() {
        return decorado.listarPersonas();
    }
}
