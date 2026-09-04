package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.BitacoraAuditoria;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de salida hexagonal que define las operaciones de persistencia
 * para la entidad Bitacora. Es implementado por los adaptadores de infraestructura.
 */
public interface BitacoraRepositorioPuerto {

    BitacoraAuditoria guardar(BitacoraAuditoria bitacora);

    List<BitacoraAuditoria> listarTodos();

    /**
     * Consulta avanzada por username, accion, entidad y rango de fechas.
     * Cualquier filtro null o vacío se ignora.
     */
    List<BitacoraAuditoria> buscarPorFiltrosAvanzado(String username, String accion, String entidad,
                                                     LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    /**
     * La bitácora es inmutable: no se actualiza ni elimina.
     */
}