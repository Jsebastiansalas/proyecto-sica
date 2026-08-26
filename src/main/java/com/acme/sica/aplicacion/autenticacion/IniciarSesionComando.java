package com.acme.sica.aplicacion.autenticacion;

/**
 * Comando de aplicación que representa la intención de iniciar sesión.
 * Es un DTO inmutable con los datos que ingresa el usuario.
 */
public class IniciarSesionComando {

    private final String username;
    private final String password;

    public IniciarSesionComando(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
