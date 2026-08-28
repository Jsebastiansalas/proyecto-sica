package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;

import java.time.LocalDateTime;

/**
 * Estrategia que cierra la visita olvidada marcándola como
 * CERRADA_POR_SISTEMA con la hora actual como salida.
 */
public class EstrategiaCierreSistema implements EstrategiaSalidaOlvidada {

    private final VisitaRepositorioPuerto visitaRepositorio;

    public EstrategiaCierreSistema(VisitaRepositorioPuerto visitaRepositorio) {
        this.visitaRepositorio = visitaRepositorio;
    }

    @Override
    public Visita regularizar(Visita visita, String motivo) {
        visita.setEstado(EstadoVisita.CERRADA_POR_SISTEMA);
        visita.setFechaHoraSalida(LocalDateTime.now());
        return visitaRepositorio.guardar(visita);
    }

    @Override
    public TipoRegularizacion getTipo() {
        return TipoRegularizacion.CIERRE_SISTEMA;
    }
}
