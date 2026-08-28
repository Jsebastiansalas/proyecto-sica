package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.visita.RechazarVisitaComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.AprobarRechazarVisitaCasoUso;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AprobarRechazarControlador {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TableView<Visita> tablaPendientes;
    @FXML private TableColumn<Visita, String> columnaDocumento;
    @FXML private TableColumn<Visita, String> columnaNombre;
    @FXML private TableColumn<Visita, String> columnaFuncionario;
    @FXML private TableColumn<Visita, String> columnaFecha;
    @FXML private TableColumn<Visita, String> columnaTipo;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;

    private AprobarRechazarVisitaCasoUso casoUso;
    private ObservableList<Visita> pendientes;

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getAprobarRechazarCasoUso();
        pendientes = FXCollections.observableArrayList();

        columnaDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getDocumentoIdentidad() : ""));
        columnaNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getNombreCompleto() : ""));
        columnaFuncionario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncionario() != null ? c.getValue().getFuncionario().getNombreCompleto() : ""));
        columnaFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHoraEsperada() != null ? c.getValue().getFechaHoraEsperada().format(FORMATO) : ""));
        columnaTipo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().name() : ""));

        tablaPendientes.setItems(pendientes);
        cargarPendientes();
    }

    @FXML
    private void aprobar() {
        etiquetaMensaje.setText("");
        Visita seleccionada = tablaPendientes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText("Seleccione una visita para aprobar");
            return;
        }

        try {
            casoUso.aprobar(seleccionada.getId());
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Visita aprobada correctamente");
            cargarPendientes();
        } catch (IllegalStateException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void rechazar() {
        etiquetaMensaje.setText("");
        Visita seleccionada = tablaPendientes.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText("Seleccione una visita para rechazar");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rechazar visita");
        dialog.setHeaderText("Rechazar visita de " + seleccionada.getPersona().getNombreCompleto());
        dialog.setContentText("Motivo del rechazo:");
        Optional<String> resultado = dialog.showAndWait();

        if (resultado.isPresent()) {
            try {
                casoUso.rechazar(new RechazarVisitaComando(seleccionada.getId(), resultado.get()));
                etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
                etiquetaMensaje.setText("Visita rechazada");
                cargarPendientes();
            } catch (IllegalStateException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
                etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
                etiquetaMensaje.setText(e.getMessage());
            }
        }
    }

    @FXML
    private void volverAlDashboard() {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml"));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
            Stage stage = (Stage) botonVolver.getScene().getWindow();
            stage.setTitle("SICA - Dashboard");
            stage.setScene(escena);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error al volver al dashboard", e);
        }
    }

    private void cargarPendientes() {
        try {
            List<Visita> lista = casoUso.listarPendientes();
            pendientes.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
