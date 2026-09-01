package com.acme.sica.infraestructura.ui.javafx;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Helper para mostrar indicadores de carga simples durante operaciones sincrónicas.
 * Deshabilita los nodos afectados y muestra un mensaje temporal en una etiqueta.
 */
public final class IndicadorCarga {

    private IndicadorCarga() {}

    /**
     * Ejecuta una operación deshabilitando temporalmente los nodos proporcionados
     * y mostrando un mensaje de carga en la etiqueta indicada.
     *
     * @param mensajeEtiqueta etiqueta donde se mostrará el mensaje
     * @param mensajeCarga    texto a mostrar mientras carga
     * @param operacion       operación a ejecutar
     * @param nodos           nodos a deshabilitar durante la operación
     */
    public static void ejecutar(Label mensajeEtiqueta, String mensajeCarga, Runnable operacion, Node... nodos) {
        String mensajeAnterior = mensajeEtiqueta.getText();
        String estiloAnterior = mensajeEtiqueta.getStyle();

        mensajeEtiqueta.setStyle("-fx-text-fill: #a78bfa;");
        mensajeEtiqueta.setText(mensajeCarga);
        for (Node nodo : nodos) {
            nodo.setDisable(true);
        }

        try {
            operacion.run();
        } finally {
            for (Node nodo : nodos) {
                nodo.setDisable(false);
            }
            if (mensajeEtiqueta.getText().equals(mensajeCarga)) {
                mensajeEtiqueta.setText(mensajeAnterior);
                mensajeEtiqueta.setStyle(estiloAnterior);
            }
        }
    }
}
