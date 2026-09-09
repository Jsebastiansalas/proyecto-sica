package com.acme.sica.aplicacion.autenticacion;

import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;

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
    private final int duracionMinutos;
    private final PuntoAcceso puntoAcceso;

    public Sesion(Long usuarioId, String username, String nombreCompleto, Set<String> permisos) {
        this(usuarioId, username, nombreCompleto, permisos, 30, PuntoAcceso.SISTEMA);
    }

    public Sesion(Long usuarioId, String username, String nombreCompleto, Set<String> permisos, int duracionMinutos) {
        this(usuarioId, username, nombreCompleto, permisos, duracionMinutos, PuntoAcceso.SISTEMA);
    }

    public Sesion(Long usuarioId, String username, String nombreCompleto, Set<String> permisos, PuntoAcceso puntoAcceso) {
        this(usuarioId, username, nombreCompleto, permisos, 30, puntoAcceso);
    }

    public Sesion(Long usuarioId, String username, String nombreCompleto, Set<String> permisos, int duracionMinutos, PuntoAcceso puntoAcceso) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.permisos = new HashSet<>(permisos != null ? permisos : Collections.emptySet());
        this.inicio = LocalDateTime.now();
        this.duracionMinutos = duracionMinutos > 0 ? duracionMinutos : 30;
        this.puntoAcceso = puntoAcceso != null ? puntoAcceso : PuntoAcceso.SISTEMA;
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

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public boolean estaExpirada() {
        return LocalDateTime.now().isAfter(inicio.plusMinutes(duracionMinutos));
    }

    public PuntoAcceso getPuntoAcceso() {
        return puntoAcceso;
    }

    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }

}
