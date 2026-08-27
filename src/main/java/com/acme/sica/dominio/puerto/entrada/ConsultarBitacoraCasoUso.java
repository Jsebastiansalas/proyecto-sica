package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.bitacora.ConsultarBitacoraComando;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;

import java.util.List;

/**
 * Puerto de entrada para la consulta de la bitácora de auditoría (HU-07).
 */
public interface ConsultarBitacoraCasoUso {

    List<BitacoraAuditoria> listarTodos();

    List<BitacoraAuditoria> consultar(ConsultarBitacoraComando filtros);
}
