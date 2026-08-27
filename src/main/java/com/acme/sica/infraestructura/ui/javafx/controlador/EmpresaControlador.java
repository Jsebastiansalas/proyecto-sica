package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.empresa.CrearEmpresaComando;
import com.acme.sica.aplicacion.empresa.EditarEmpresaComando;
import com.acme.sica.dominio.excepciones.EmpresaEnUsoExcepcion;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.puerto.entrada.GestionarEmpresaCasoUso;
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

public class EmpresaControlador {

    @FXML
    private TableView<Empresa> tablaEmpresas;

    @FXML
    private TableColumn<Empresa, String> columnaNombre;

    @FXML
    private TableColumn<Empresa, String> columnaUbicacion;

    @FXML
    private TableColumn<Empresa, String> columnaEstado;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoUbicacion;

    @FXML
    private CheckBox checkActiva;

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

    private GestionarEmpresaCasoUso casoUso;
    private ObservableList<Empresa> empresas;
    private Empresa empresaSeleccionada;

    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarEmpresaCasoUso();
        this.empresas = FXCollections.observableArrayList();

        columnaNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        columnaUbicacion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUbicacion()));
        columnaEstado.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().isActiva() ? "Activa" : "Inactiva"));

        tablaEmpresas.setItems(empresas);
        tablaEmpresas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.empresaSeleccionada = nuevo;
            if (nuevo != null) {
                campoNombre.setText(nuevo.getNombre());
                campoUbicacion.setText(nuevo.getUbicacion());
                checkActiva.setSelected(nuevo.isActiva());
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarEmpresas();
    }

    @FXML
    private void guardarEmpresa() {
        etiquetaMensaje.setText("");

        String nombre = campoNombre.getText().trim();
        String ubicacion = campoUbicacion.getText().trim();

        if (nombre.isEmpty()) {
            etiquetaMensaje.setText("El nombre de la empresa es obligatorio");
            return;
        }

        try {
            if (empresaSeleccionada == null) {
                casoUso.crear(new CrearEmpresaComando(nombre, ubicacion));
                etiquetaMensaje.setText("Empresa creada correctamente");
            } else {
                casoUso.editar(new EditarEmpresaComando(
                        empresaSeleccionada.getId(), nombre, ubicacion, checkActiva.isSelected()));
                etiquetaMensaje.setText("Empresa actualizada correctamente");
            }
            limpiarFormulario();
            cargarEmpresas();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion
                 | EmpresaEnUsoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void eliminarEmpresa() {
        etiquetaMensaje.setText("");

        if (empresaSeleccionada == null) {
            etiquetaMensaje.setText("Seleccione una empresa para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar la empresa '" + empresaSeleccionada.getNombre() + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    casoUso.eliminar(empresaSeleccionada.getId());
                    etiquetaMensaje.setText("Empresa eliminada correctamente");
                    limpiarFormulario();
                    cargarEmpresas();
                } catch (PermisoDenegadoExcepcion | EmpresaEnUsoExcepcion
                         | EntidadNoEncontradaExcepcion e) {
                    etiquetaMensaje.setText(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        empresaSeleccionada = null;
        tablaEmpresas.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoUbicacion.clear();
        checkActiva.setSelected(true);
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

    private void cargarEmpresas() {
        try {
            List<Empresa> lista = casoUso.listarTodos();
            empresas.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
