package com.acme.sica.aplicacion.visita;

import com.acme.sica.dominio.modelo.Activo;
import java.util.List;
import java.util.Collections;

/**
 * Objeto de comando (DTO) que transporta los datos necesarios para registrar el check-in de un invitado.
 */
public class CheckInInvitadoComando {

    private final String documento;
    private final String placaVehiculo;
    private final String marcaVehiculo;
    private final String tipoVehiculo;
    private final List<Activo> activos;

    public CheckInInvitadoComando(String documento) {
        this(documento, null, null, null, Collections.emptyList());
    }

    public CheckInInvitadoComando(String documento, String placaVehiculo, String marcaVehiculo, String tipoVehiculo) {
        this(documento, placaVehiculo, marcaVehiculo, tipoVehiculo, Collections.emptyList());
    }

    public CheckInInvitadoComando(String documento, String placaVehiculo, String marcaVehiculo, String tipoVehiculo, List<Activo> activos) {
        this.documento = documento;
        this.placaVehiculo = placaVehiculo;
        this.marcaVehiculo = marcaVehiculo;
        this.tipoVehiculo = tipoVehiculo;
        this.activos = activos != null ? activos : Collections.emptyList();
    }

    public String getDocumento() {
        return documento;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public List<Activo> getActivos() {
        return activos;
    }
}
