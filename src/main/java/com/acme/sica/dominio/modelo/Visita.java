package com.acme.sica.dominio.modelo;

import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;

import java.time.LocalDateTime;

public class Visita {

    private Long id;
    private Persona persona;
    private Empresa empresa;
    private Funcionario funcionario;
    private String motivo;
    private EstadoVisita estado;
    private LocalDateTime fechaHoraIngreso;
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraEsperada;
    private Usuario registradaPor;
    private LocalDateTime fechaCreacion;

    public Visita() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoVisita.PENDIENTE_APROBACION;
    }

    public Visita(Persona persona, Empresa empresa, Funcionario funcionario, String motivo,
                  LocalDateTime fechaHoraEsperada, Usuario registradaPor) {
        this();
        this.persona = persona;
        this.empresa = empresa;
        this.funcionario = funcionario;
        this.motivo = motivo;
        this.fechaHoraEsperada = fechaHoraEsperada;
        this.registradaPor = registradaPor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoVisita getEstado() {
        return estado;
    }

    public void setEstado(EstadoVisita estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public LocalDateTime getFechaHoraEsperada() {
        return fechaHoraEsperada;
    }

    public void setFechaHoraEsperada(LocalDateTime fechaHoraEsperada) {
        this.fechaHoraEsperada = fechaHoraEsperada;
    }

    public Usuario getRegistradaPor() {
        return registradaPor;
    }

    public void setRegistradaPor(Usuario registradaPor) {
        this.registradaPor = registradaPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean estaDentro() {
        return this.estado == EstadoVisita.DENTRO;
    }

    public boolean estaCerrada() {
        return this.estado == EstadoVisita.CERRADA || this.estado == EstadoVisita.CERRADA_POR_SISTEMA;
    }

    public boolean puedeHacerCheckIn() {
        return this.estado == EstadoVisita.APROBADO;
    }

    @Override
    public String toString() {
        return "Visita{" +
                "id=" + id +
                ", persona=" + (persona != null ? persona.getDocumentoIdentidad() + " - " + persona.getNombreCompleto() : null) +
                ", empresa=" + (empresa != null ? empresa.getNombre() : null) +
                ", estado=" + estado +
                ", fechaHoraIngreso=" + fechaHoraIngreso +
                '}';
    }
}