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
import com.acme.sica.dominio.modelo.Activo;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la interfaz JavaFX para check in.
 * Recibe eventos de la vista y delega la lógica a los casos de uso de aplicación.
 */
public class CheckInControlador {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TextField campoDocumento;
    @FXML private TextField campoPlacaVehiculo;
    @FXML private TextField campoMarcaVehiculo;
    @FXML private TextField campoTipoVehiculo;
    @FXML private TextField campoDescripcionActivo;
    @FXML private TextField campoSerieActivo;
    @FXML private Label etiquetaActivosDeclarados;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;

    private final List<Activo> activosPendientes = new ArrayList<>();

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

    /**
     * Inicializa el controlador y configura los componentes de la vista.
     */
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
    private void agregarActivo() {
        if (campoDescripcionActivo == null) return;
        String desc = campoDescripcionActivo.getText().trim();
        String serie = campoSerieActivo != null ? campoSerieActivo.getText().trim() : "";
        if (desc.isEmpty()) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText("Ingrese al menos la descripción del activo");
            return;
        }
        activosPendientes.add(new Activo(desc, serie));
        campoDescripcionActivo.clear();
        if (campoSerieActivo != null) campoSerieActivo.clear();
        if (etiquetaActivosDeclarados != null) {
            etiquetaActivosDeclarados.setText(activosPendientes.size() + " activos declarados");
        }
        etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
        etiquetaMensaje.setText("Activo añadido a la lista temporal");
    }

    @FXML
    private void confirmarIngreso() {
        etiquetaMensaje.setText("");
        String documento = campoDocumento.getText().trim();
        String placaVehiculo = campoPlacaVehiculo != null && campoPlacaVehiculo.getText() != null ? campoPlacaVehiculo.getText().trim() : null;
        String marcaVehiculo = campoMarcaVehiculo != null && campoMarcaVehiculo.getText() != null ? campoMarcaVehiculo.getText().trim() : null;
        String tipoVehiculo = campoTipoVehiculo != null && campoTipoVehiculo.getText() != null ? campoTipoVehiculo.getText().trim() : null;

        if (documento.isEmpty()) {
            etiquetaMensaje.setText("Ingrese el documento del invitado");
            return;
        }

        try {
            Visita visita = casoUso.checkIn(new CheckInInvitadoComando(
                    documento, placaVehiculo, marcaVehiculo, tipoVehiculo, new ArrayList<>(activosPendientes)
            ));
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Ingreso confirmado: " + visita.getPersona().getNombreCompleto());
            campoDocumento.clear();
            if (campoPlacaVehiculo != null) campoPlacaVehiculo.clear();
            if (campoMarcaVehiculo != null) campoMarcaVehiculo.clear();
            if (campoTipoVehiculo != null) campoTipoVehiculo.clear();
            activosPendientes.clear();
            if (etiquetaActivosDeclarados != null) {
                etiquetaActivosDeclarados.setText("0 activos declarados");
            }
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
