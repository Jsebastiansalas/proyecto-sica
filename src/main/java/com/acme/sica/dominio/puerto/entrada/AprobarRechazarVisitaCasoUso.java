package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.RechazarVisitaComando;
import com.acme.sica.dominio.modelo.Visita;

import java.util.List;

/**
 * Puerto de entrada para aprobar o rechazar visitas pendientes (HU-14).
 */
public interface AprobarRechazarVisitaCasoUso {

    Visita aprobar(Long visitaId);

    Visita rechazar(RechazarVisitaComando comando);

    List<Visita> listarPendientes();
}
