package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.visita.RechazarVisitaComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.AprobarRechazarVisitaCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.DialogoConfirmacion;
import com.acme.sica.infraestructura.ui.javafx.Mensajes;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        String persona = seleccionada.getPersona() != null ? seleccionada.getPersona().getNombreCompleto() : "la visita";
        DialogoConfirmacion.confirmar(
                Mensajes.get("dialogo.confirmar.aprobacion.titulo"),
                "¿Aprobar la visita de " + persona + "?",
                () -> ejecutarAprobacion(seleccionada.getId())
        );
    }

    private void ejecutarAprobacion(Long visitaId) {
        try {
            casoUso.aprobar(visitaId);
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

        String persona = seleccionada.getPersona() != null ? seleccionada.getPersona().getNombreCompleto() : "la visita";
        DialogoConfirmacion.confirmar(
                Mensajes.get("dialogo.confirmar.rechazo.titulo"),
                "¿Rechazar la visita de " + persona + "?",
                () -> solicitarMotivoRechazo(seleccionada)
        );
    }

    private void solicitarMotivoRechazo(Visita seleccionada) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rechazar visita");
        dialog.setHeaderText("Rechazar visita de " + seleccionada.getPersona().getNombreCompleto());
        dialog.setContentText("Motivo del rechazo:");
        Optional<String> resultado = dialog.showAndWait();

        resultado.ifPresent(motivo -> ejecutarRechazo(seleccionada.getId(), motivo));
    }

    private void ejecutarRechazo(Long visitaId, String motivo) {
        try {
            casoUso.rechazar(new RechazarVisitaComando(visitaId, motivo));
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Visita rechazada");
            cargarPendientes();
        } catch (IllegalStateException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(botonVolver);
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
