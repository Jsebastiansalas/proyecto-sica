package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.reporte.ConsultarReporteAccesosComando;
import com.acme.sica.dominio.modelo.Visita;
import java.util.List;

/**
 * Puerto de entrada hexagonal que define el caso de uso para consultar el reporte de accesos.
 * Es implementado por la capa de aplicación.
 */
public interface ConsultarReporteAccesosCasoUso {
    List<Visita> consultar(ConsultarReporteAccesosComando comando);
}
