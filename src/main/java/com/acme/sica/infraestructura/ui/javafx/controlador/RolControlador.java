package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.rol.CrearRolComando;
import com.acme.sica.aplicacion.rol.EditarRolComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.excepciones.RolEnUsoExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RolControlador {

    @FXML
    private TableView<Rol> tablaRoles;

    @FXML
    private TableColumn<Rol, String> columnaNombre;

    @FXML
    private TableColumn<Rol, String> columnaDescripcion;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoDescripcion;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonGuardar;

    @FXML
    private Button botonEliminar;

    @FXML
    private Button botonLimpiar;

    @FXML
    private Button botonVolver;

    private GestionarRolCasoUso casoUso;
    private ObservableList<Rol> roles;
    private Rol rolSeleccionado;

    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarRolCasoUso();
        this.roles = FXCollections.observableArrayList();

        columnaNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        columnaDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescripcion()));

        tablaRoles.setItems(roles);
        tablaRoles.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.rolSeleccionado = nuevo;
            if (nuevo != null) {
                campoNombre.setText(nuevo.getNombre());
                campoDescripcion.setText(nuevo.getDescripcion());
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarRoles();
    }

    @FXML
    private void guardarRol() {
        etiquetaMensaje.setText("");

        String nombre = campoNombre.getText().trim();
        String descripcion = campoDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            etiquetaMensaje.setText("El nombre del rol es obligatorio");
            return;
        }

        try {
            if (rolSeleccionado == null) {
                CrearRolComando comando = new CrearRolComando(nombre, descripcion);
                casoUso.crear(comando);
                etiquetaMensaje.setText("Rol creado correctamente");
            } else {
                EditarRolComando comando = new EditarRolComando(rolSeleccionado.getId(), nombre, descripcion);
                casoUso.editar(comando);
                etiquetaMensaje.setText("Rol actualizado correctamente");
            }
            limpiarFormulario();
            cargarRoles();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion | RolEnUsoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void eliminarRol() {
        etiquetaMensaje.setText("");

        if (rolSeleccionado == null) {
            etiquetaMensaje.setText("Seleccione un rol para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar el rol '" + rolSeleccionado.getNombre() + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    casoUso.eliminar(rolSeleccionado.getId());
                    etiquetaMensaje.setText("Rol eliminado correctamente");
                    limpiarFormulario();
                    cargarRoles();
                } catch (PermisoDenegadoExcepcion | RolEnUsoExcepcion | EntidadNoEncontradaExcepcion e) {
                    etiquetaMensaje.setText(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        rolSeleccionado = null;
        tablaRoles.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoDescripcion.clear();
        botonGuardar.setText("Guardar");
        etiquetaMensaje.setText("");
    }

    @FXML
    private void volverAlDashboard() {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            Stage stage = (Stage) etiquetaMensaje.getScene().getWindow();
            stage.setTitle("SICA - Dashboard");
            stage.setScene(escena);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error al volver al dashboard", e);
        }
    }

    private void cargarRoles() {
        try {
            List<Rol> lista = casoUso.listarTodos();
            roles.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

}
