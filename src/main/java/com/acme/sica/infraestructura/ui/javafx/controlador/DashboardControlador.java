package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardControlador {

    @FXML
    private Label etiquetaBienvenida;

    @FXML
    private Label etiquetaRol;

    @FXML
    private Button botonGestionarRoles;

    @FXML
    private Button botonGestionarPermisos;

    @FXML
    private Button botonAsignarRoles;

    @FXML
    private Button botonConsultarBitacora;

    @FXML
    private Button botonCerrarSesion;

    @FXML
    public void initialize() {
        SesionContexto.obtener().ifPresent(sesion -> {
            etiquetaBienvenida.setText("Bienvenido, " + sesion.getNombreCompleto());
            etiquetaRol.setText("Usuario: " + sesion.getUsername());
        });

        botonGestionarRoles.setVisible(SesionContexto.tienePermiso("gestionar_roles"));
        botonGestionarPermisos.setVisible(SesionContexto.tienePermiso("gestionar_permisos"));
        botonAsignarRoles.setVisible(SesionContexto.tienePermiso("asignar_roles"));
        botonConsultarBitacora.setVisible(SesionContexto.tienePermiso("consultar_bitacora"));
    }

    @FXML
    private void abrirGestionRoles() {
        cargarVista("/fxml/roles.fxml", "SICA - Gestión de Roles");
    }

    @FXML
    private void abrirGestionPermisos() {
        cargarVista("/fxml/permisos.fxml", "SICA - Gestión de Permisos");
    }

    @FXML
    private void abrirAsignacionRoles() {
        cargarVista("/fxml/asignar-roles.fxml", "SICA - Asignar Roles");
    }

    @FXML
    private void abrirConsultarBitacora() {
        cargarVista("/fxml/bitacora.fxml", "SICA - Bitácora de Auditoría");
    }

    @FXML
    private void cerrarSesion() {
        SesionContexto.cerrar();
        cargarVista("/fxml/login.fxml", "SICA - Zona Acme");
    }

    private void cargarVista(String rutaFxml, String titulo) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource(rutaFxml));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            Stage stage = (Stage) etiquetaBienvenida.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(escena);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la vista: " + rutaFxml, e);
        }
    }

}
