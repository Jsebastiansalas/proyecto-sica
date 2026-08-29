package com.acme.sica.aplicacion.usuario;

import java.util.Set;

public class CrearUsuarioComando {

    private final String username;
    private final String password;
    private final String nombreCompleto;
    private final Set<Long> rolIds;

    public CrearUsuarioComando(String username, String password, String nombreCompleto, Set<Long> rolIds) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.rolIds = rolIds;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public Set<Long> getRolIds() { return rolIds; }
}