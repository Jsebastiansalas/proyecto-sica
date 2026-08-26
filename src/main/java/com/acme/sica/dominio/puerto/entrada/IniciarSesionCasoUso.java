package com.acme.sica.dominio.puerto.entrada;

import com.acme.sica.aplicacion.autenticacion.IniciarSesionComando;
import com.acme.sica.aplicacion.autenticacion.LoginResultado;

/**
 * Puerto de entrada del dominio para el caso de uso de inicio de sesión.
 * Define el contrato que cualquier adaptador de login debe cumplir.
 */
public interface IniciarSesionCasoUso {

    LoginResultado ejecutar(IniciarSesionComando comando);

}
