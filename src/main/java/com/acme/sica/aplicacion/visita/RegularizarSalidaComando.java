package com.acme.sica.aplicacion.visita;

public class RegularizarSalidaComando {

    private final String documento;
    private final TipoRegularizacion tipo;
    private final String motivo;

    public RegularizarSalidaComando(String documento, TipoRegularizacion tipo, String motivo) {
        this.documento = documento;
        this.tipo = tipo;
        this.motivo = motivo;
    }

    public String getDocumento() { return documento; }
    public TipoRegularizacion getTipo() { return tipo; }
    public String getMotivo() { return motivo; }
}
