package com.acme.sica.aplicacion.usuario;

import java.util.Set;

public class EditarUsuarioComando {

    private final Long id;
    private final String username;
    private final String nombreCompleto;
    private final String password;
    private final boolean activo;
    private final Set<Long> rolIds;

    public EditarUsuarioComando(Long id, String username, String nombreCompleto,
                                 String password, boolean activo, Set<Long> rolIds) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.password = password;
        this.activo = activo;
        this.rolIds = rolIds;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getPassword() { return password; }
    public boolean isActivo() { return activo; }
    public Set<Long> getRolIds() { return rolIds; }
}