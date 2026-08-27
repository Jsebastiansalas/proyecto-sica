package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.PreRegistrarInvitadoComando;
import com.acme.sica.dominio.modelo.Visita;

/**
 * Puerto de entrada para pre-registrar una visita de invitado (HU-11).
 */
public interface PreRegistrarInvitadoCasoUso {

    Visita preRegistrar(PreRegistrarInvitadoComando comando);
}
