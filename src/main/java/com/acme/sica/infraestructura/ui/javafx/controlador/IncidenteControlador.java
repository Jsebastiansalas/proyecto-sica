package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.incidente.CrearIncidenteComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;
import com.acme.sica.dominio.puerto.entrada.GestionarIncidenteCasoUso;
import com.acme.sica.dominio.puerto.salida.PersonaRepositorioPuerto;
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
import java.time.format.DateTimeFormatter;

public class IncidenteControlador {

    @FXML private ComboBox<Persona> comboPersonas;
    @FXML private ComboBox<GravedadIncidente> comboGravedad;
    @FXML private TextArea campoDescripcion;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;
    @FXML private TableView<Incidente> tablaIncidentes;
    @FXML private TableColumn<Incidente, String> columnaFecha;
    @FXML private TableColumn<Incidente, String> columnaPersona;
    @FXML private TableColumn<Incidente, String> columnaGravedad;
    @FXML private TableColumn<Incidente, String> columnaDescripcion;
    @FXML private TableColumn<Incidente, String> columnaUsuario;

    private GestionarIncidenteCasoUso casoUso;
    private PersonaRepositorioPuerto personaRepositorio;
    private final ObservableList<Incidente> incidentes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarIncidenteCasoUso();
        personaRepositorio = AplicacionJavaFx.getContenedorDependencias().getPersonaRepositorio();
        comboGravedad.getItems().setAll(GravedadIncidente.values());
        comboGravedad.setConverter(new StringConverter<>() {
            public String toString(GravedadIncidente value) { return value == null ? "" : value.name(); }
            public GravedadIncidente fromString(String value) { return null; }
        });
        comboPersonas.setConverter(new StringConverter<>() {
            public String toString(Persona value) {
                return value == null ? "" : value.getDocumentoIdentidad() + " - " + value.getNombreCompleto();
            }
            public Persona fromString(String value) { return null; }
        });
        columnaFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaCreacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        columnaPersona.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersona().getNombreCompleto()));
        columnaGravedad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGravedad().name()));
        columnaDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        columnaUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRegistradoPor().getUsername()));
        tablaIncidentes.setItems(incidentes);
        cargarPersonas();
        cargarIncidentes();
    }

    @FXML
    private void registrar() {
        etiquetaMensaje.setText("");
        Persona persona = comboPersonas.getValue();
        String descripcion = campoDescripcion.getText().trim();
        GravedadIncidente gravedad = comboGravedad.getValue();
        if (persona == null || descripcion.isEmpty() || gravedad == null) {
            etiquetaMensaje.setText("Persona, descripción y gravedad son obligatorios");
            return;
        }
        try {
            casoUso.crear(new CrearIncidenteComando(persona.getId(), descripcion, gravedad));
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Incidente registrado correctamente");
            campoDescripcion.clear();
            comboPersonas.setValue(null);
            comboGravedad.setValue(null);
            cargarIncidentes();
        } catch (IllegalArgumentException | IllegalStateException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void cargarPersonas() {
        comboPersonas.getItems().setAll(personaRepositorio.listarTodos());
    }

    private void cargarIncidentes() {
        try { incidentes.setAll(casoUso.listarTodos()); }
        catch (PermisoDenegadoExcepcion e) { etiquetaMensaje.setText(e.getMessage()); }
    }

    @FXML
    private void volverAlDashboard() {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setScene(escena); stage.setTitle("SICA - Dashboard"); stage.setResizable(true); stage.show();
        } catch (IOException e) { throw new RuntimeException("Error al volver al dashboard", e); }
    }
}
