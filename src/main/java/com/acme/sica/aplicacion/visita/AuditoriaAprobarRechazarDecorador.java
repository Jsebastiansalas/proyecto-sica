package com.acme.sica.aplicacion.visita;

import com.acme.sica.aplicacion.auditoria.RegistradorAuditoria;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoAccionAuditoria;
import com.acme.sica.dominio.puerto.entrada.AprobarRechazarVisitaCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;

import java.util.List;

/**
 * Decorador de auditoría para aprobar/rechazar visitas (HU-06).
 */
public class AuditoriaAprobarRechazarDecorador implements AprobarRechazarVisitaCasoUso {

    private static final String ENTIDAD = "VISITA";

    private final AprobarRechazarVisitaCasoUso decorado;
    private final BitacoraRepositorioPuerto bitacoraRepositorio;

    public AuditoriaAprobarRechazarDecorador(AprobarRechazarVisitaCasoUso decorado,
                                              BitacoraRepositorioPuerto bitacoraRepositorio) {
        this.decorado = decorado;
        this.bitacoraRepositorio = bitacoraRepositorio;
    }

    /**
     * Aprueba la visita identificada.
     */
    @Override
    public Visita aprobar(Long visitaId) {
        Visita visita = decorado.aprobar(visitaId);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.APROBAR_VISITA,
                ENTIDAD, visita.getId(), "Visita aprobada: " + visita.getPersona().getNombreCompleto());
        return visita;
    }

    /**
     * Rechaza la visita identificada.
     */
    @Override
    public Visita rechazar(RechazarVisitaComando comando) {
        Visita visita = decorado.rechazar(comando);
        RegistradorAuditoria.registrar(bitacoraRepositorio, TipoAccionAuditoria.RECHAZAR_VISITA,
                ENTIDAD, visita.getId(), "Visita rechazada: " + visita.getPersona().getNombreCompleto());
        return visita;
    }

    /**
     * Obtiene el listado de entidades pendientes.
     */
    @Override
    public List<Visita> listarPendientes() {
        return decorado.listarPendientes();
    }
}
