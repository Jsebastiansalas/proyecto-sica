package com.acme.sica.dominio.modelo.enumerados;

/**
 * Enumerado que representa los posibles valores correspondientes al estado de una visita en el dominio.
 */
public enum EstadoVisita {
    PENDIENTE_APROBACION,
    PENDIENTE_APROBACION_OLVIDO,
    APROBADO,
    DENTRO,
    CERRADA,
    CERRADA_POR_SISTEMA,
    RECHAZADO
}