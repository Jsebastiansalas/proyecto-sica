package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.persona.CrearPersonaComando;
import com.acme.sica.aplicacion.persona.EditarPersonaComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.entrada.GestionarPersonaCasoUso;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.List;

public class PersonaControlador {

    @FXML
    private TableView<Persona> tablaPersonas;

    @FXML
    private TableColumn<Persona, String> columnaDocumento;

    @FXML
    private TableColumn<Persona, String> columnaNombre;

    @FXML
    private TableColumn<Persona, String> columnaTipo;

    @FXML
    private TableColumn<Persona, String> columnaEstado;

    @FXML
    private TextField campoDocumento;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoFotoUrl;

    @FXML
    private ComboBox<TipoPersona> comboTipo;

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

    private GestionarPersonaCasoUso casoUso;
    private ObservableList<Persona> personas;
    private Persona personaSeleccionada;

    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarPersonaCasoUso();
        this.personas = FXCollections.observableArrayList();

        comboTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoPersona tipo) {
                return tipo == null ? "" : tipo.name();
            }

            @Override
            public TipoPersona fromString(String texto) {
                return null;
            }
        });
        comboTipo.getItems().setAll(TipoPersona.values());

        columnaDocumento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDocumentoIdentidad()));
        columnaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        columnaTipo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTipo() != null ? c.getValue().getTipo().name() : ""));
        columnaEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isBloqueada() ? "Bloqueada" : "Activa"));

        tablaPersonas.setItems(personas);
        tablaPersonas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.personaSeleccionada = nuevo;
            if (nuevo != null) {
                campoDocumento.setText(nuevo.getDocumentoIdentidad());
                campoNombre.setText(nuevo.getNombreCompleto());
                campoFotoUrl.setText(nuevo.getFotoUrl());
                comboTipo.setValue(nuevo.getTipo());
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarPersonas();
    }

    @FXML
    private void guardarPersona() {
        etiquetaMensaje.setText("");

        String documento = campoDocumento.getText().trim();
        String nombre = campoNombre.getText().trim();
        String fotoUrl = campoFotoUrl.getText().trim();
        TipoPersona tipo = comboTipo.getValue();

        if (documento.isEmpty()) {
            etiquetaMensaje.setText("El documento de identidad es obligatorio");
            return;
        }
        if (nombre.isEmpty()) {
            etiquetaMensaje.setText("El nombre completo es obligatorio");
            return;
        }
        if (tipo == null) {
            etiquetaMensaje.setText("El tipo de persona es obligatorio");
            return;
        }

        try {
            if (personaSeleccionada == null) {
                casoUso.crear(new CrearPersonaComando(tipo, documento, nombre, fotoUrl));
                etiquetaMensaje.setText("Persona creada correctamente");
            } else {
                casoUso.editar(new EditarPersonaComando(personaSeleccionada.getId(), documento,
                        nombre, fotoUrl, tipo, personaSeleccionada.isBloqueada()));
                etiquetaMensaje.setText("Persona actualizada correctamente");
            }
            limpiarFormulario();
            cargarPersonas();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void eliminarPersona() {
        etiquetaMensaje.setText("");

        if (personaSeleccionada == null) {
            etiquetaMensaje.setText("Seleccione una persona para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar a '" + personaSeleccionada.getNombreCompleto() + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    casoUso.eliminar(personaSeleccionada.getId());
                    etiquetaMensaje.setText("Persona eliminada correctamente");
                    limpiarFormulario();
                    cargarPersonas();
                } catch (PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
                    etiquetaMensaje.setText(e.getMessage());
                }
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        personaSeleccionada = null;
        tablaPersonas.getSelectionModel().clearSelection();
        campoDocumento.clear();
        campoNombre.clear();
        campoFotoUrl.clear();
        comboTipo.setValue(null);
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

    private void cargarPersonas() {
        try {
            List<Persona> lista = casoUso.listarTodos();
            personas.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
