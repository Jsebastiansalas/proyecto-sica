package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.bitacora.ConsultarBitacoraComando;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.puerto.entrada.ConsultarBitacoraCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BitacoraControlador {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private TableView<BitacoraAuditoria> tablaBitacora;

    @FXML
    private TableColumn<BitacoraAuditoria, String> columnaFecha;

    @FXML
    private TableColumn<BitacoraAuditoria, String> columnaUsuario;

    @FXML
    private TableColumn<BitacoraAuditoria, String> columnaAccion;

    @FXML
    private TableColumn<BitacoraAuditoria, String> columnaEntidad;

    @FXML
    private TableColumn<BitacoraAuditoria, String> columnaDetalle;

    @FXML
    private TextField campoUsername;

    @FXML
    private TextField campoAccion;

    @FXML
    private TextField campoEntidad;

    @FXML
    private DatePicker campoFechaDesde;

    @FXML
    private DatePicker campoFechaHasta;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonVolver;

    private ConsultarBitacoraCasoUso casoUso;
    private ObservableList<BitacoraAuditoria> registros;

    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getConsultarBitacoraCasoUso();
        this.registros = FXCollections.observableArrayList();

        columnaFecha.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getFechaHora() != null ? cell.getValue().getFechaHora().format(FORMATO_FECHA) : ""));
        columnaUsuario.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getUsuarioNombre() != null ? cell.getValue().getUsuarioNombre() : ""));
        columnaAccion.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getAccion() != null ? cell.getValue().getAccion() : ""));
        columnaEntidad.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEntidad() != null ? cell.getValue().getEntidad() : ""));
        columnaDetalle.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDetalles() != null ? cell.getValue().getDetalles() : ""));

        tablaBitacora.setItems(registros);
        cargarRegistros();
    }

    @FXML
    private void buscar() {
        etiquetaMensaje.setText("");

        String username = vacioONull(campoUsername.getText());
        String accion = vacioONull(campoAccion.getText());
        String entidad = vacioONull(campoEntidad.getText());
        LocalDateTime fechaDesde = campoFechaDesde.getValue() != null
                ? campoFechaDesde.getValue().atStartOfDay() : null;
        LocalDateTime fechaHasta = campoFechaHasta.getValue() != null
                ? campoFechaHasta.getValue().atTime(23, 59, 59) : null;

        ConsultarBitacoraComando filtros = new ConsultarBitacoraComando(
                username, accion, entidad, fechaDesde, fechaHasta);

        try {
            List<BitacoraAuditoria> lista = casoUso.consultar(filtros);
            registros.setAll(lista);
            etiquetaMensaje.setText(lista.size() + " registro(s) encontrado(s)");
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void limpiarFiltros() {
        campoUsername.clear();
        campoAccion.clear();
        campoEntidad.clear();
        campoFechaDesde.setValue(null);
        campoFechaHasta.setValue(null);
        etiquetaMensaje.setText("");
        cargarRegistros();
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }

    private void cargarRegistros() {
        try {
            List<BitacoraAuditoria> lista = casoUso.listarTodos();
            registros.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private String vacioONull(String texto) {
        return (texto == null || texto.trim().isEmpty()) ? null : texto.trim();
    }
}
