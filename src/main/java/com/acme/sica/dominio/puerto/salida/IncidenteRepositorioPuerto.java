package com.acme.sica.dominio.puerto.salida;

import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncidenteRepositorioPuerto {

    Incidente guardar(Incidente incidente);

    Optional<Incidente> buscarPorId(Long id);

    List<Incidente> listarTodos();

    List<Incidente> buscarPorPersona(Long personaId);

    List<Incidente> buscarPorFiltros(LocalDateTime fechaDesde, LocalDateTime fechaHasta,
                                     Long empresaId, GravedadIncidente gravedad);

    void eliminarPorId(Long id);
}