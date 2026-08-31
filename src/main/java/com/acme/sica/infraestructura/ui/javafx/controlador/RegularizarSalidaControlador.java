package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.visita.RegularizarSalidaComando;
import com.acme.sica.aplicacion.visita.TipoRegularizacion;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.RegularizarSalidaCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RegularizarSalidaControlador {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TextField campoDocumento;
    @FXML private ComboBox<TipoRegularizacion> comboTipo;
    @FXML private TextArea campoMotivo;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;

    @FXML private TableView<Visita> tablaOlvidadas;
    @FXML private TableColumn<Visita, String> columnaDocumento;
    @FXML private TableColumn<Visita, String> columnaNombre;
    @FXML private TableColumn<Visita, String> columnaIngreso;

    private RegularizarSalidaCasoUso casoUso;
    private ObservableList<Visita> olvidadas;

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getRegularizarSalidaCasoUso();
        olvidadas = FXCollections.observableArrayList();

        comboTipo.getItems().setAll(TipoRegularizacion.values());
        comboTipo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(TipoRegularizacion t) {
                if (t == null) return "";
                return t == TipoRegularizacion.CIERRE_SISTEMA
                        ? "Cierre por Sistema" : "Nuevo Ingreso";
            }

            @Override
            public TipoRegularizacion fromString(String s) { return null; }
        });

        columnaDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getDocumentoIdentidad() : ""));
        columnaNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getNombreCompleto() : ""));
        columnaIngreso.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHoraIngreso() != null ? c.getValue().getFechaHoraIngreso().format(FORMATO) : ""));

        tablaOlvidadas.setItems(olvidadas);
        cargarOlvidadas();
    }

    @FXML
    private void regularizar() {
        etiquetaMensaje.setText("");
        etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");

        String documento = campoDocumento.getText().trim();
        TipoRegularizacion tipo = comboTipo.getValue();
        String motivo = campoMotivo.getText();

        if (documento.isEmpty()) {
            etiquetaMensaje.setText("Ingrese el documento");
            return;
        }
        if (tipo == null) {
            etiquetaMensaje.setText("Seleccione el tipo de regularización");
            return;
        }

        try {
            casoUso.regularizar(new RegularizarSalidaComando(documento, tipo, motivo));
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Salida regularizada correctamente (" + tipo + ")");
            limpiarFormulario();
            cargarOlvidadas();
        } catch (IllegalArgumentException | IllegalStateException
                 | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        campoDocumento.clear();
        comboTipo.setValue(null);
        campoMotivo.clear();
        etiquetaMensaje.setText("");
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(botonVolver);
    }

    private void cargarOlvidadas() {
        try {
            List<Visita> lista = casoUso.listarSalidasOlvidadas();
            olvidadas.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
