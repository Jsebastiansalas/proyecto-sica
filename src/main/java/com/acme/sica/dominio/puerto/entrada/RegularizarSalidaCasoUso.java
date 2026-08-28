package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.visita.RegularizarSalidaComando;
import com.acme.sica.dominio.modelo.Visita;

import java.util.List;

/**
 * Puerto de entrada para regularizar salidas olvidadas (HU-16).
 */
public interface RegularizarSalidaCasoUso {

    Visita regularizar(RegularizarSalidaComando comando);

    List<Visita> listarSalidasOlvidadas();
}
