package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.reporte.ConsultarReporteIncidentesComando;
import com.acme.sica.dominio.modelo.Incidente;
import java.util.List;

/**
 * Puerto de entrada hexagonal que define el caso de uso para consultar el reporte de incidentes.
 * Es implementado por la capa de aplicación.
 */
public interface ConsultarReporteIncidentesCasoUso {
    List<Incidente> consultar(ConsultarReporteIncidentesComando comando);
}
