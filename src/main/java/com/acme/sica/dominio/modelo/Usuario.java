package com.acme.sica.dominio.modelo;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad de dominio que representa un usuario.
 * Forma parte del núcleo del modelo de negocio del sistema.
 */
public class Usuario {

    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private Set<Rol> roles = new HashSet<>();
    private Set<Permiso> permisosDirectos = new HashSet<>();

    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.activo = true;
    }

    public Usuario(String username, String password, String nombreCompleto) {
        this();
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public void agregarRol(Rol rol) {
        this.roles.add(rol);
    }

    public void removerRol(Rol rol) {
        this.roles.remove(rol);
    }

    public Set<Permiso> getPermisosDirectos() {
        return permisosDirectos;
    }

    public void setPermisosDirectos(Set<Permiso> permisosDirectos) {
        this.permisosDirectos = permisosDirectos;
    }

    public void agregarPermisoDirecto(Permiso permiso) {
        this.permisosDirectos.add(permiso);
    }

    public boolean tienePermiso(String nombrePermiso) {
        return roles.stream()
                .flatMap(rol -> rol.getPermisos().stream())
                .anyMatch(p -> p.getNombre().equals(nombrePermiso));
    }

    public boolean tieneRol(String nombreRol) {
        return roles.stream()
                .anyMatch(r -> r.getNombre().equals(nombreRol));
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", activo=" + activo +
                '}';
    }
}