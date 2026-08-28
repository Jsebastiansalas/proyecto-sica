package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;

import java.time.LocalDateTime;

/**
 * Estrategia que cierra la visita olvidada (CERRADA_POR_SISTEMA) y
 * crea una nueva visita pendiente de aprobación para que la persona
 * reingrese por el flujo normal.
 */
public class EstrategiaNuevoIngreso implements EstrategiaSalidaOlvidada {

    private final VisitaRepositorioPuerto visitaRepositorio;

    public EstrategiaNuevoIngreso(VisitaRepositorioPuerto visitaRepositorio) {
        this.visitaRepositorio = visitaRepositorio;
    }

    @Override
    public Visita regularizar(Visita visita, String motivo) {
        visita.setEstado(EstadoVisita.CERRADA_POR_SISTEMA);
        visita.setFechaHoraSalida(LocalDateTime.now());
        visita.setMotivo((visita.getMotivo() != null ? visita.getMotivo() + " | " : "")
                + "Salida olvidada: " + (motivo != null ? motivo : "sin motivo"));
        visitaRepositorio.guardar(visita);

        Visita nuevoIngreso = new Visita(visita.getPersona(), null,
                visita.getFuncionario(),
                "Reingreso tras salida olvidada",
                LocalDateTime.now(), null);
        nuevoIngreso.setEstado(EstadoVisita.PENDIENTE_APROBACION);
        return visitaRepositorio.guardar(nuevoIngreso);
    }

    @Override
    public TipoRegularizacion getTipo() {
        return TipoRegularizacion.NUEVO_INGRESO;
    }
}
