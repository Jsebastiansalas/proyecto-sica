package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.reporte.ConsultarReporteIncidentesComando;
import com.acme.sica.dominio.modelo.Incidente;
import java.util.List;

public interface ConsultarReporteIncidentesCasoUso {
    List<Incidente> consultar(ConsultarReporteIncidentesComando comando);
}
