package com.acme.sica;

import com.acme.sica.infraestructura.configuracion.ConfiguracionBaseDatos;
import com.acme.sica.infraestructura.configuracion.ContenedorDependencias;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;

import javafx.application.Application;

/**
 * Clase pública que proporciona el punto de entrada de la aplicación.
 */
public class Main {

    public static void main(String[] args) {
        ConfiguracionBaseDatos configuracion = new ConfiguracionBaseDatos();
        ContenedorDependencias contenedor = new ContenedorDependencias(configuracion);

        contenedor.getInicializadorBaseDatos().inicializar();

        AplicacionJavaFx.setContenedorDependencias(contenedor);
        Application.launch(AplicacionJavaFx.class, args);
    }

}