package com.acme.sica.aplicacion.autenticacion;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa una sesión de usuario autenticado en memoria.
 * Contiene sus datos y permisos para evitar consultar la BD repetidamente.
 */
public class Sesion {

    private final Long usuarioId;
    private final String username;
    private final String nombreCompleto;
    private final Set<String> permisos;
    private final LocalDateTime inicio;

    public Sesion(Long usuarioId, String username, String nombreCompleto, Set<String> permisos) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.permisos = new HashSet<>(permisos != null ? permisos : Collections.emptySet());
        this.inicio = LocalDateTime.now();
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public Set<String> getPermisos() {
        return Collections.unmodifiableSet(permisos);
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }

}
