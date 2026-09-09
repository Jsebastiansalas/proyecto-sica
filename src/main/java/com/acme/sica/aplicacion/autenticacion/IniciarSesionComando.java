package com.acme.sica.aplicacion.autenticacion;

import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;

/**
 * Comando de aplicación que representa la intención de iniciar sesión.
 * Es un DTO inmutable con los datos que ingresa el usuario.
 */
public class IniciarSesionComando {

    private final String username;
    private final String password;
    private final PuntoAcceso puntoAcceso;

    public IniciarSesionComando(String username, String password) {
        this(username, password, PuntoAcceso.SISTEMA);
    }

    public IniciarSesionComando(String username, String password, PuntoAcceso puntoAcceso) {
        this.username = username;
        this.password = password;
        this.puntoAcceso = puntoAcceso;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public PuntoAcceso getPuntoAcceso() {
        return puntoAcceso;
    }

}
