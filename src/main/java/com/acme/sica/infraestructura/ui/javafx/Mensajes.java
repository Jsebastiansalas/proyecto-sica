package com.acme.sica.infraestructura.ui.javafx;

import java.util.ResourceBundle;

/**
 * Helper para acceder a los mensajes internacionalizados de la aplicación.
 */
public final class Mensajes {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("i18n/messages");

    private Mensajes() {}

    /**
     * Obtiene un mensaje internacionalizado por su clave.
     */
    public static String get(String clave) {
        return BUNDLE.getString(clave);
    }

    /**
     * Obtiene un mensaje internacionalizado por su clave.
     */
    public static String get(String clave, Object... args) {
        return String.format(BUNDLE.getString(clave), args);
    }
}
