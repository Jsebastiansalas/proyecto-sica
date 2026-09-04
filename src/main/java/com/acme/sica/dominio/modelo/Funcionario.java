package com.acme.sica.dominio.modelo;

/**
 * Entidad de dominio que representa un funcionario.
 * Forma parte del núcleo del modelo de negocio del sistema.
 */
public class Funcionario {

    private Long id;
    private Usuario usuario;
    private Empresa empresa;
    private String nombreCompleto;
    private String cargo;
    private boolean activo;

    public Funcionario() {
        this.activo = true;
    }

    public Funcionario(Usuario usuario, Empresa empresa, String nombreCompleto, String cargo) {
        this();
        this.usuario = usuario;
        this.empresa = empresa;
        this.nombreCompleto = nombreCompleto;
        this.cargo = cargo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", cargo='" + cargo + '\'' +
                ", activo=" + activo +
                '}';
    }
}