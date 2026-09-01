package com.acme.sica.infraestructura.ui.javafx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Helper para mostrar diálogos de confirmación reutilizables.
 */
public final class DialogoConfirmacion {

    private DialogoConfirmacion() {}

    /**
     * Muestra un diálogo de confirmación y ejecuta la acción si el usuario confirma.
     *
     * @param titulo     título del diálogo
     * @param cabecera   texto principal del diálogo
     * @param contenido  texto descriptivo adicional
     * @param accion     acción a ejecutar si el usuario presiona OK
     */
    public static void confirmar(String titulo, String cabecera, String contenido, Runnable accion) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle(titulo);
        confirmacion.setHeaderText(cabecera);
        confirmacion.setContentText(contenido);

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                accion.run();
            }
        });
    }

    /**
     * Muestra un diálogo de confirmación con contenido por defecto.
     */
    public static void confirmar(String titulo, String cabecera, Runnable accion) {
        confirmar(titulo, cabecera, Mensajes.get("dialogo.confirmar.contenido"), accion);
    }

    /**
     * Muestra un diálogo de confirmación genérico para eliminar una entidad.
     *
     * @param nombreEntidad nombre descriptivo de la entidad a eliminar
     * @param accion        acción a ejecutar si el usuario confirma
     */
    public static void confirmarEliminacion(String nombreEntidad, Runnable accion) {
        confirmar(
                Mensajes.get("dialogo.confirmar.eliminar.titulo"),
                "¿Eliminar " + nombreEntidad + "?",
                Mensajes.get("dialogo.confirmar.eliminar.contenido"),
                accion
        );
    }

    /**
     * Muestra un diálogo de confirmación para cerrar sesión.
     *
     * @param accion acción a ejecutar si el usuario confirma
     */
    public static void confirmarCerrarSesion(Runnable accion) {
        confirmar(
                Mensajes.get("dialogo.confirmar.cerrar_sesion.titulo"),
                Mensajes.get("dialogo.confirmar.cerrar_sesion.cabecera"),
                Mensajes.get("dialogo.confirmar.cerrar_sesion.contenido"),
                accion
        );
    }

    /**
     * Muestra un diálogo de confirmación simple y retorna si el usuario aceptó.
     *
     * @param titulo    título del diálogo
     * @param cabecera  texto principal
     * @param contenido texto descriptivo
     * @return true si el usuario presionó OK, false en caso contrario
     */
    public static boolean preguntar(String titulo, String cabecera, String contenido) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle(titulo);
        confirmacion.setHeaderText(cabecera);
        confirmacion.setContentText(contenido);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
