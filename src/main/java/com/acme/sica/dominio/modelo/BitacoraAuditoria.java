package com.acme.sica.dominio.modelo;

import java.time.LocalDateTime;

public class BitacoraAuditoria {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String accion;
    private String entidad;
    private Long entidadId;
    private String detalles;
    private String ipAddress;
    private LocalDateTime fechaHora;

    public BitacoraAuditoria() {
        this.fechaHora = LocalDateTime.now();
    }

    public BitacoraAuditoria(Long usuarioId, String usuarioNombre, String accion,
                             String entidad, Long entidadId, String detalles, String ipAddress) {
        this();
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.detalles = detalles;
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}