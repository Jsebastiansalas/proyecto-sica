package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.autenticacion.IniciarSesionComando;
import com.acme.sica.aplicacion.autenticacion.LoginResultado;
import com.acme.sica.dominio.excepciones.CredencialesInvalidasExcepcion;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginControlador {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private Label etiquetaMensaje;

    private IniciarSesionCasoUso casoUso;

    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getIniciarSesionCasoUso();
    }

    @FXML
    private void manejarLogin() {
        etiquetaMensaje.setText("");

        String username = campoUsuario.getText().trim();
        String password = campoContrasena.getText();

        if (username.isEmpty() || password.isEmpty()) {
            etiquetaMensaje.setText("Ingrese usuario y contraseña");
            return;
        }

        IniciarSesionComando comando = new IniciarSesionComando(username, password);

        try {
            LoginResultado resultado = casoUso.ejecutar(comando);
            etiquetaMensaje.setText("Bienvenido, " + resultado.getNombreCompleto());
            // TODO: navegar al dashboard según el rol
        } catch (CredencialesInvalidasExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

}
