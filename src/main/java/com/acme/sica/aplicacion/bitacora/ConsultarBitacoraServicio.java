package com.acme.sica.aplicacion.bitacora;

import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.puerto.entrada.ConsultarBitacoraCasoUso;
import com.acme.sica.dominio.puerto.salida.BitacoraRepositorioPuerto;
import com.acme.sica.infraestructura.seguridad.autorizacion.ManejadorAutorizacion;

import java.util.List;

/**
 * Servicio de aplicación para la consulta de la bitácora de auditoría (HU-07).
 *
 * Requiere el permiso {@code consultar_bitacora}, validado mediante la
 * cadena de responsabilidad (HU-05). La bitácora es de solo lectura.
 */
public class ConsultarBitacoraServicio implements ConsultarBitacoraCasoUso {

    private static final String PERMISO_REQUERIDO = "consultar_bitacora";

    private final BitacoraRepositorioPuerto bitacoraRepositorio;
    private final ManejadorAutorizacion cadenaAutorizacion;

    public ConsultarBitacoraServicio(BitacoraRepositorioPuerto bitacoraRepositorio,
                                     ManejadorAutorizacion cadenaAutorizacion) {
        this.bitacoraRepositorio = bitacoraRepositorio;
        this.cadenaAutorizacion = cadenaAutorizacion;
    }

    /**
     * Obtiene el listado completo de entidades disponibles.
     */
    @Override
    public List<BitacoraAuditoria> listarTodos() {
        autorizar("consultar bitácora");
        return bitacoraRepositorio.listarTodos();
    }

    /**
     * Ejecuta la consulta correspondiente y retorna los resultados.
     */
    @Override
    public List<BitacoraAuditoria> consultar(ConsultarBitacoraComando filtros) {
        autorizar("consultar bitácora");
        return bitacoraRepositorio.buscarPorFiltrosAvanzado(
                filtros.getUsername(),
                filtros.getAccion(),
                filtros.getEntidad(),
                filtros.getFechaDesde(),
                filtros.getFechaHasta()
        );
    }

    private void autorizar(String accion) {
        cadenaAutorizacion.verificar(PERMISO_REQUERIDO, accion);
    }
}
