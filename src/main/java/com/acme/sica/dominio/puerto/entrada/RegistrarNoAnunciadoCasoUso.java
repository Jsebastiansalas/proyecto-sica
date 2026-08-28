package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.RegistrarNoAnunciadoComando;
import com.acme.sica.dominio.modelo.Visita;

/**
 * Puerto de entrada para registrar un invitado no anunciado (HU-13).
 */
public interface RegistrarNoAnunciadoCasoUso {

    Visita registrar(RegistrarNoAnunciadoComando comando);
}
