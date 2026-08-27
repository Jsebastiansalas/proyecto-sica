package com.acme.sica.aplicacion.funcionario;

public class CrearFuncionarioComando {

    private final Long usuarioId;
    private final Long empresaId;
    private final String nombreCompleto;
    private final String cargo;

    public CrearFuncionarioComando(Long usuarioId, Long empresaId, String nombreCompleto, String cargo) {
        this.usuarioId = usuarioId;
        this.empresaId = empresaId;
        this.nombreCompleto = nombreCompleto;
        this.cargo = cargo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCargo() {
        return cargo;
    }
}
