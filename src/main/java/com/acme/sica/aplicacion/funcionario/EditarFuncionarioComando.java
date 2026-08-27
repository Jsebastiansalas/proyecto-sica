package com.acme.sica.aplicacion.funcionario;

public class EditarFuncionarioComando {

    private final Long id;
    private final Long usuarioId;
    private final Long empresaId;
    private final String nombreCompleto;
    private final String cargo;
    private final boolean activo;

    public EditarFuncionarioComando(Long id, Long usuarioId, Long empresaId,
                                    String nombreCompleto, String cargo, boolean activo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.empresaId = empresaId;
        this.nombreCompleto = nombreCompleto;
        this.cargo = cargo;
        this.activo = activo;
    }

    public Long getId() {
        return id;
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

    public boolean isActivo() {
        return activo;
    }
}
