package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.CheckOutComando;
import com.acme.sica.dominio.modelo.Visita;

import java.util.List;

/**
 * Puerto de entrada para el check-out de personas (HU-17).
 */
public interface CheckOutCasoUso {

    Visita checkOut(CheckOutComando comando);

    List<Visita> listarDentro();
}
