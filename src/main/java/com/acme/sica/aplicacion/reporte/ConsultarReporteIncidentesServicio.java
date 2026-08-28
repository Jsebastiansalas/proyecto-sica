package com.acme.sica.aplicacion.reporte;

import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.puerto.entrada.ConsultarReporteIncidentesCasoUso;
import com.acme.sica.dominio.puerto.salida.IncidenteRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;
import java.util.List;

public class ConsultarReporteIncidentesServicio implements ConsultarReporteIncidentesCasoUso {
    private final IncidenteRepositorioPuerto incidenteRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public ConsultarReporteIncidentesServicio(IncidenteRepositorioPuerto incidenteRepositorio,
                                              ManejadorAutorizacion cadenaAutorizacion) {
        this.incidenteRepositorio = incidenteRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    @Override
    public List<Incidente> consultar(ConsultarReporteIncidentesComando comando) {
        cadenaAutorizacion.verificar("generar_reporte_incidentes", "generar reporte de incidentes");
        return incidenteRepositorio.buscarPorFiltros(comando.getFechaDesde(), comando.getFechaHasta(),
                comando.getEmpresaId(), comando.getGravedad());
    }
}
