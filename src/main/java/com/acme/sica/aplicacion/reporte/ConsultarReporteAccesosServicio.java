package com.acme.sica.aplicacion.reporte;

import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.ConsultarReporteAccesosCasoUso;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import java.util.List;

/**
 * Servicio de aplicación encargado de consultar el reporte de accesos.
 * Contiene la lógica de negocio y coordina los puertos de entrada y salida.
 */
public class ConsultarReporteAccesosServicio implements ConsultarReporteAccesosCasoUso {
    private final VisitaRepositorioPuerto visitaRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public ConsultarReporteAccesosServicio(VisitaRepositorioPuerto visitaRepositorio,
                                           ManejadorAutorizacion cadenaAutorizacion) {
        this.visitaRepositorio = visitaRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Ejecuta la consulta correspondiente y retorna los resultados.
     */
    @Override
    public List<Visita> consultar(ConsultarReporteAccesosComando comando) {
        cadenaAutorizacion.verificar("generar_reporte_accesos", "generar reporte de accesos");
        return visitaRepositorio.buscarPorFiltros(comando.getFechaDesde(), comando.getFechaHasta(), comando.getEmpresaId(), comando.getPuntoAcceso());
    }
}
