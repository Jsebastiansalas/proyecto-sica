package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.BitacoraAuditoria;

import java.time.LocalDateTime;
import java.util.List;

public interface BitacoraRepositorioPuerto {

    BitacoraAuditoria guardar(BitacoraAuditoria bitacora);

    List<BitacoraAuditoria> listarTodos();

    List<BitacoraAuditoria> buscarPorFiltros(Long usuarioId, String entidad,
                                             LocalDateTime fechaDesde, LocalDateTime fechaHasta);

    /**
     * La bitácora es inmutable: no se actualiza ni elimina.
     */
}