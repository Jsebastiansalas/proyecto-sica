package com.acme.sica.aplicacion.incidente;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.GestionarIncidenteCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

public class AuditoriaIncidenteDecorador implements GestionarIncidenteCasoUso {

    private final GestionarIncidenteCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaIncidenteDecorador(GestionarIncidenteCasoUso decorado,
                                       BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    @Override
    public Incidente crear(CrearIncidenteComando comando) {
        Incidente incidente = decorado.crear(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.CREAR_INCIDENTE,
                "INCIDENTE", incidente.getId(), "Incidente registrado: " + incidente.getDescripcion());
        return incidente;
    }

    @Override
    public List<Incidente> listarTodos() {
        return decorado.listarTodos();
    }

    @Override
    public List<com.acme.sica.dominio.modelo.Persona> listarPersonas() {
        return decorado.listarPersonas();
    }
}
