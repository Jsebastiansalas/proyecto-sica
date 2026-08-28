package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.RegistrarTrabajadorComando;
import com.acme.sica.dominio.modelo.Visita;

/**
 * Puerto de entrada para registrar el ingreso de un trabajador (HU-15).
 */
public interface RegistrarTrabajadorCasoUso {

    Visita registrar(RegistrarTrabajadorComando comando);
}
