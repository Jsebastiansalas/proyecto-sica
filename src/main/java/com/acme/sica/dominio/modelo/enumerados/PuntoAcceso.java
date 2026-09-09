package com.acme.sica.dominio.modelo.enumerados;

import java.util.Arrays;

/**
 * Representa los puntos de acceso o puertas por donde una persona puede ingresar
 * o salir de la instalación. Se usa en la sesión y en la bitácora de auditoría.
 */
public enum PuntoAcceso {
    PUERTA_PRINCIPAL("Puerta Principal"),
    RECEPCION("Recepción"),
    PUERTA_NORTE("Puerta Norte"),
    PUERTA_SUR("Puerta Sur"),
    SOTANO_VEHICULAR("Sótano / Vehicular"),
    SISTEMA("Sistema");

    private final String etiqueta;

    PuntoAcceso(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Resuelve un enum a partir de su nombre o de su etiqueta legible.
     * Acepta tanto "PUERTA_PRINCIPAL" como "Puerta Principal", y es tolerante
     * a valores nulos o desconocidos (devuelve SISTEMA por defecto).
     */
    public static PuntoAcceso fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return SISTEMA;
        }
        String normalizado = valor.trim();
        try {
            return PuntoAcceso.valueOf(normalizado);
        } catch (IllegalArgumentException e) {
            return Arrays.stream(values())
                    .filter(p -> p.etiqueta.equalsIgnoreCase(normalizado)
                            || p.name().replace("_", " ").equalsIgnoreCase(normalizado))
                    .findFirst()
                    .orElse(SISTEMA);
        }
    }
}
