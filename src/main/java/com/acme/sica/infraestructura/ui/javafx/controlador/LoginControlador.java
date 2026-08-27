package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.autenticacion.IniciarSesionComando;
import com.acme.sica.aplicacion.autenticacion.LoginResultado;
import com.acme.sica.dominio.excepciones.CredencialesInvalidasExcepcion;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
            navegarAlDashboard();
        } catch (CredencialesInvalidasExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void navegarAlDashboard() {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            Stage stage = (Stage) etiquetaMensaje.getScene().getWindow();
            stage.setTitle("SICA - Dashboard");
            stage.setScene(escena);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar el dashboard", e);
        }
    }

}
