package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.autenticacion.IniciarSesionComando;
import com.acme.sica.aplicacion.autenticacion.LoginResultado;
import com.acme.sica.dominio.excepciones.CredencialesInvalidasExcepcion;
import com.acme.sica.dominio.modelo.enumerados.PuntoAcceso;
import com.acme.sica.dominio.puerto.entrada.IniciarSesionCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Controlador de la interfaz JavaFX para login.
 * Recibe eventos de la vista y delega la lógica a los casos de uso de aplicación.
 */
public class LoginControlador {

    @FXML
    private TextField campoUsuario;

    @FXML
    private PasswordField campoContrasena;

    @FXML
    private TextField campoContrasenaVisible;

    @FXML
    private Button botonMostrarContrasena;

    @FXML
    private ComboBox<PuntoAcceso> comboPuntoAcceso;

    @FXML
    private Label etiquetaMensaje;

    private IniciarSesionCasoUso casoUso;
    private boolean contrasenaVisible = false;

    /**
     * Inicializa el controlador y configura los componentes de la vista.
     */
    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getIniciarSesionCasoUso();
        campoContrasenaVisible.setVisible(false);
        campoContrasenaVisible.setManaged(false);

        if (comboPuntoAcceso != null) {
            comboPuntoAcceso.setConverter(new StringConverter<>() {
                @Override
                public String toString(PuntoAcceso punto) {
                    return punto == null ? "" : punto.getEtiqueta();
                }

                @Override
                public PuntoAcceso fromString(String text) {
                    return PuntoAcceso.fromString(text);
                }
            });
            comboPuntoAcceso.getItems().setAll(PuntoAcceso.PUERTA_PRINCIPAL, PuntoAcceso.RECEPCION, PuntoAcceso.PUERTA_NORTE, PuntoAcceso.PUERTA_SUR, PuntoAcceso.SOTANO_VEHICULAR);
            comboPuntoAcceso.setValue(PuntoAcceso.PUERTA_PRINCIPAL);
        }
    }

    @FXML
    private void toggleContrasena() {
        contrasenaVisible = !contrasenaVisible;

        if (contrasenaVisible) {
            campoContrasenaVisible.setText(campoContrasena.getText());
            campoContrasenaVisible.setVisible(true);
            campoContrasenaVisible.setManaged(true);
            campoContrasena.setVisible(false);
            campoContrasena.setManaged(false);
            botonMostrarContrasena.setText("🙈");
        } else {
            campoContrasena.setText(campoContrasenaVisible.getText());
            campoContrasena.setVisible(true);
            campoContrasena.setManaged(true);
            campoContrasenaVisible.setVisible(false);
            campoContrasenaVisible.setManaged(false);
            botonMostrarContrasena.setText("👁");
        }
    }

    @FXML
    private void manejarLogin() {
        etiquetaMensaje.setText("");

        String username = campoUsuario.getText().trim();
        String password = contrasenaVisible ? campoContrasenaVisible.getText() : campoContrasena.getText();

        if (username.isEmpty() || password.isEmpty()) {
            etiquetaMensaje.setText("Ingrese usuario y contraseña");
            return;
        }

        PuntoAcceso puntoAcceso = (comboPuntoAcceso != null && comboPuntoAcceso.getValue() != null)
                ? comboPuntoAcceso.getValue()
                : PuntoAcceso.PUERTA_PRINCIPAL;

        IniciarSesionComando comando = new IniciarSesionComando(username, password, puntoAcceso);

        try {
            LoginResultado resultado = casoUso.ejecutar(comando);
            navegarAlDashboard();
        } catch (CredencialesInvalidasExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        } catch (Exception e) {
            etiquetaMensaje.setText("Error al abrir el dashboard: " + e.getMessage());
        }
    }

    private void navegarAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }

}
