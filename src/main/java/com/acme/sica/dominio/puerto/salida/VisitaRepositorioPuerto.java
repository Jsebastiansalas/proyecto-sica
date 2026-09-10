package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida hexagonal que define las operaciones de persistencia
 * para la entidad Visita. Es implementado por los adaptadores de infraestructura.
 */
public interface VisitaRepositorioPuerto {

    Visita guardar(Visita visita);

    Optional<Visita> buscarPorId(Long id);

    List<Visita> buscarPorPersona(Long personaId);

    List<Visita> buscarPorPersonaYEstado(Long personaId, EstadoVisita estado);

    Optional<Visita> buscarVisitaAbiertaPorPersona(Long personaId);

    List<Visita> buscarPendientesPorEmpresa(Long empresaId);

    List<Visita> listarTodos();

    default List<Visita> buscarPorFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long empresaId) {
        return buscarPorFiltros(fechaDesde, fechaHasta, empresaId, null);
    }

    List<Visita> buscarPorFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta, Long empresaId, PuntoAcceso puntoAcceso);

    long contarPendientes();

    void eliminarPorId(Long id);

    List<Visita> consultarPersonalPresentePorEmpresa(Long empresaId);
}