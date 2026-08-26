package com.acme.sica.aplicacion.autenticacion;

import java.util.Set;

/**
 * Resultado de un login exitoso.
 * Contiene los datos mínimos que la interfaz necesita para mostrar la sesión.
 */
public class LoginResultado {

    private final Long usuarioId;
    private final String nombreCompleto;
    private final String username;
    private final Set<String> permisos;

    public LoginResultado(Long usuarioId, String nombreCompleto, String username, Set<String> permisos) {
        this.usuarioId = usuarioId;
        this.nombreCompleto = nombreCompleto;
        this.username = username;
        this.permisos = permisos;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getPermisos() {
        return permisos;
    }

}
