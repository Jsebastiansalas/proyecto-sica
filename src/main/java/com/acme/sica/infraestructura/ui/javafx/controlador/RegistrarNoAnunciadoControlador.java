package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.visita.RegistrarNoAnunciadoComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.entrada.RegistrarNoAnunciadoCasoUso;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
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
import java.util.List;

public class RegistrarNoAnunciadoControlador {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML private TextField campoDocumento;
    @FXML private TextField campoNombre;
    @FXML private TextField campoFotoUrl;
    @FXML private ComboBox<Funcionario> comboFuncionarios;
    @FXML private TextArea campoMotivo;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;

    @FXML private TableView<Visita> tablaNoAnunciados;
    @FXML private TableColumn<Visita, String> columnaDocumento;
    @FXML private TableColumn<Visita, String> columnaNombre;
    @FXML private TableColumn<Visita, String> columnaFuncionario;
    @FXML private TableColumn<Visita, String> columnaFecha;

    private RegistrarNoAnunciadoCasoUso casoUso;
    private FuncionarioRepositorioPuerto funcionarioRepositorio;
    private VisitaRepositorioPuerto visitaRepositorio;
    private ObservableList<Visita> noAnunciados;

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getRegistrarNoAnunciadoCasoUso();
        funcionarioRepositorio = AplicacionJavaFx.getContenedorDependencias().getFuncionarioRepositorio();
        visitaRepositorio = AplicacionJavaFx.getContenedorDependencias().getVisitaRepositorio();
        noAnunciados = FXCollections.observableArrayList();

        comboFuncionarios.setConverter(new StringConverter<>() {
            @Override
            public String toString(Funcionario f) {
                return f == null ? "" : f.getNombreCompleto() + " (" + f.getEmpresa().getNombre() + ")";
            }

            @Override
            public Funcionario fromString(String texto) { return null; }
        });

        columnaDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getDocumentoIdentidad() : ""));
        columnaNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getNombreCompleto() : ""));
        columnaFuncionario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncionario() != null ? c.getValue().getFuncionario().getNombreCompleto() : ""));
        columnaFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHoraEsperada() != null ? c.getValue().getFechaHoraEsperada().format(FORMATO) : ""));

        tablaNoAnunciados.setItems(noAnunciados);
        cargarFuncionarios();
        cargarNoAnunciados();
    }

    @FXML
    private void registrar() {
        etiquetaMensaje.setText("");
        etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");

        String documento = campoDocumento.getText().trim();
        String nombre = campoNombre.getText().trim();
        String fotoUrl = campoFotoUrl.getText().trim();
        Funcionario funcionario = comboFuncionarios.getValue();
        String motivo = campoMotivo.getText();

        if (documento.isEmpty()) {
            etiquetaMensaje.setText("El documento es obligatorio");
            return;
        }
        if (nombre.isEmpty()) {
            etiquetaMensaje.setText("El nombre es obligatorio");
            return;
        }
        if (funcionario == null) {
            etiquetaMensaje.setText("Seleccione un funcionario responsable");
            return;
        }

        try {
            casoUso.registrar(new RegistrarNoAnunciadoComando(
                    documento, nombre, fotoUrl, funcionario.getId(), motivo));
            etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
            etiquetaMensaje.setText("Invitado no anunciado registrado. Queda pendiente de aprobación.");
            limpiarFormulario();
            cargarNoAnunciados();
        } catch (IllegalArgumentException | IllegalStateException
                 | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        campoDocumento.clear();
        campoNombre.clear();
        campoFotoUrl.clear();
        comboFuncionarios.setValue(null);
        campoMotivo.clear();
        etiquetaMensaje.setText("");
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

    private void cargarFuncionarios() {
        try {
            List<Funcionario> funcionarios = funcionarioRepositorio.listarTodos().stream()
                    .filter(Funcionario::isActivo)
                    .toList();
            comboFuncionarios.getItems().setAll(funcionarios);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void cargarNoAnunciados() {
        try {
            List<Visita> lista = visitaRepositorio.listarTodos().stream()
                    .filter(v -> v.getEstado() == EstadoVisita.PENDIENTE_APROBACION_OLVIDO)
                    .toList();
            noAnunciados.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
