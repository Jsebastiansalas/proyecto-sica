package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.CheckInInvitadoComando;
import com.acme.sica.dominio.modelo.Visita;

import java.util.List;

/**
 * Puerto de entrada para el check-in de un invitado (HU-12).
 */
public interface CheckInInvitadoCasoUso {

    Visita checkIn(CheckInInvitadoComando comando);

    List<Visita> listarAprobadas();

    List<Visita> listarDentro();
}
