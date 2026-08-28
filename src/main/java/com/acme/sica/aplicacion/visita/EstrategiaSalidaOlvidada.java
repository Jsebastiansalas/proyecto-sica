package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.modelo.Visita;

/**
 * Estrategia para regularizar una salida olvidada (HU-16).
 *
 * Patrón Strategy: cada implementación encapsula un algoritmo distinto
 * de regularización. El servicio delega a la estrategia seleccionada.
 */
public interface EstrategiaSalidaOlvidada {

    /**
     * Regulariza una visita con salida olvidada.
     *
     * @param visita  la visita abierta (estado DENTRO)
     * @param motivo  motivo de la regularización
     * @return la visita regularizada (cerrada)
     */
    Visita regularizar(Visita visita, String motivo);

    TipoRegularizacion getTipo();
}
