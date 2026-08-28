package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.reporte.ConsultarReporteAccesosComando;
import com.acme.sica.dominio.modelo.Visita;
import java.util.List;

public interface ConsultarReporteAccesosCasoUso {
    List<Visita> consultar(ConsultarReporteAccesosComando comando);
}
