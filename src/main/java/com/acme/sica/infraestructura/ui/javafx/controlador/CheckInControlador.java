package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.visita.CheckInInvitadoComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.CheckInInvitadoCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckInControlador {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TextField campoDocumento;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;

    @FXML private TableView<Visita> tablaAprobadas;
    @FXML private TableColumn<Visita, String> columnaAprobadaDocumento;
    @FXML private TableColumn<Visita, String> columnaAprobadaNombre;
    @FXML private TableColumn<Visita, String> columnaAprobadaFuncionario;

    @FXML private TableView<Visita> tablaDentro;
    @FXML private TableColumn<Visita, String> columnaDentroDocumento;
    @FXML private TableColumn<Visita, String> columnaDentroNombre;
    @FXML private TableColumn<Visita, String> columnaDentroIngreso;

    private CheckInInvitadoCasoUso casoUso;
    private ObservableList<Visita> aprobadas;
    private ObservableList<Visita> dentro;

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getCheckInInvitadoCasoUso();
        aprobadas = FXCollections.observableArrayList();
        dentro = FXCollections.observableArrayList();

        configurarColumnas();
        tablaAprobadas.setItems(aprobadas);
        tablaDentro.setItems(dentro);
        cargarVisitas();
    }

    private void configurarColumnas() {
        columnaAprobadaDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getDocumentoIdentidad() : ""));
        columnaAprobadaNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getNombreCompleto() : ""));
        columnaAprobadaFuncionario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncionario() != null ? c.getValue().getFuncionario().getNombreCompleto() : ""));

        columnaDentroDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getDocumentoIdentidad() : ""));
        columnaDentroNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getNombreCompleto() : ""));
        columnaDentroIngreso.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHoraIngreso() != null ? c.getValue().getFechaHoraIngreso().format(FORMATO) : ""));
    }

    @FXML
    private void confirmarIngreso() {
        etiquetaMensaje.setText("");
        String documento = campoDocumento.getText().trim();

        if (documento.isEmpty()) {
            etiquetaMensaje.setText("Ingrese el documento del invitado");
            return;
        }

        try {
            Visita visita = casoUso.checkIn(new CheckInInvitadoComando(documento));
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Ingreso confirmado: " + visita.getPersona().getNombreCompleto());
            campoDocumento.clear();
            cargarVisitas();
        } catch (IllegalArgumentException | IllegalStateException
                 | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(botonVolver);
    }

    private void cargarVisitas() {
        try {
            List<Visita> listaAprobadas = casoUso.listarAprobadas();
            aprobadas.setAll(listaAprobadas);
            List<Visita> listaDentro = casoUso.listarDentro();
            dentro.setAll(listaDentro);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
