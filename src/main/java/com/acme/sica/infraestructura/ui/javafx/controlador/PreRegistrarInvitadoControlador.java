package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.visita.PreRegistrarInvitadoComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.entrada.PreRegistrarInvitadoCasoUso;
import com.acme.sica.dominio.puerto.salida.FuncionarioRepositorioPuerto;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PreRegistrarInvitadoControlador {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private TableView<Visita> tablaVisitas;

    @FXML
    private TableColumn<Visita, String> columnaPersona;

    @FXML
    private TableColumn<Visita, String> columnaDocumento;

    @FXML
    private TableColumn<Visita, String> columnaFoto;

    @FXML
    private TableColumn<Visita, String> columnaFuncionario;

    @FXML
    private TableColumn<Visita, String> columnaEmpresa;

    @FXML
    private TableColumn<Visita, String> columnaFecha;

    @FXML
    private TableColumn<Visita, String> columnaEstado;

    @FXML
    private ComboBox<Persona> comboPersonas;

    @FXML
    private ComboBox<Funcionario> comboFuncionarios;

    @FXML
    private DatePicker campoFecha;

    @FXML
    private TextField campoHora;

    @FXML
    private TextArea campoMotivo;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonVolver;

    private PreRegistrarInvitadoCasoUso casoUso;
    private PersonaRepositorioPuerto personaRepositorio;
    private FuncionarioRepositorioPuerto funcionarioRepositorio;
    private ObservableList<Visita> visitas;

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getPreRegistrarInvitadoCasoUso();
        personaRepositorio = AplicacionJavaFx.getContenedorDependencias().getPersonaRepositorio();
        funcionarioRepositorio = AplicacionJavaFx.getContenedorDependencias().getFuncionarioRepositorio();
        visitas = FXCollections.observableArrayList();

        configurarCombos();
        configurarColumnas();

        tablaVisitas.setItems(visitas);
        campoHora.setText("09:00");

        cargarPersonas();
        cargarFuncionarios();
        cargarVisitas();
    }

    private void configurarCombos() {
        comboPersonas.setConverter(new StringConverter<>() {
            @Override
            public String toString(Persona persona) {
                return persona == null ? "" : persona.getDocumentoIdentidad() + " - " + persona.getNombreCompleto();
            }

            @Override
            public Persona fromString(String texto) {
                return null;
            }
        });

        comboFuncionarios.setConverter(new StringConverter<>() {
            @Override
            public String toString(Funcionario funcionario) {
                return funcionario == null ? "" : funcionario.getNombreCompleto()
                        + " (" + funcionario.getEmpresa().getNombre() + ")";
            }

            @Override
            public Funcionario fromString(String texto) {
                return null;
            }
        });
    }

    private void configurarColumnas() {
        columnaDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getDocumentoIdentidad() : ""));
        columnaPersona.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null ? c.getValue().getPersona().getNombreCompleto() : ""));
        columnaFoto.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getPersona() != null && c.getValue().getPersona().getFotoUrl() != null
                        ? c.getValue().getPersona().getFotoUrl() : "Sin foto"));
        columnaFuncionario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncionario() != null ? c.getValue().getFuncionario().getNombreCompleto() : ""));
        columnaEmpresa.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFuncionario() != null && c.getValue().getFuncionario().getEmpresa() != null
                        ? c.getValue().getFuncionario().getEmpresa().getNombre() : ""));
        columnaFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaHoraEsperada() != null
                        ? c.getValue().getFechaHoraEsperada().toLocalDate().toString() + " "
                        + c.getValue().getFechaHoraEsperada().toLocalTime().toString()
                        : ""));
        columnaEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().name() : ""));
    }

    @FXML
    private void preRegistrar() {
        etiquetaMensaje.setText("");

        Persona persona = comboPersonas.getValue();
        Funcionario funcionario = comboFuncionarios.getValue();
        LocalDate fecha = campoFecha.getValue();
        String horaTexto = campoHora.getText();
        String motivo = campoMotivo.getText();

        if (persona == null) {
            etiquetaMensaje.setText("Seleccione una persona");
            return;
        }
        if (funcionario == null) {
            etiquetaMensaje.setText("Seleccione un funcionario responsable");
            return;
        }
        if (fecha == null) {
            etiquetaMensaje.setText("Seleccione una fecha");
            return;
        }

        LocalTime hora;
        try {
            hora = LocalTime.parse(horaTexto.trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            etiquetaMensaje.setText("Formato de hora inválido. Use HH:mm");
            return;
        }

        LocalDateTime fechaHoraEsperada = LocalDateTime.of(fecha, hora);

        try {
            casoUso.preRegistrar(new PreRegistrarInvitadoComando(
                    persona.getId(), funcionario.getId(), fechaHoraEsperada, motivo));
            etiquetaMensaje.setText("Invitado pre-registrado correctamente");
            limpiarFormulario();
            cargarVisitas();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        comboPersonas.setValue(null);
        comboFuncionarios.setValue(null);
        campoFecha.setValue(null);
        campoHora.setText("09:00");
        campoMotivo.clear();
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
            List<Persona> personas = personaRepositorio.listarTodos().stream()
                    .filter(p -> p.getTipo() == TipoPersona.INVITADO && !p.isBloqueada())
                    .toList();
            comboPersonas.getItems().setAll(personas);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
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

    private void cargarVisitas() {
        // Carga las visitas pre-registradas recientes listando todas.
        // Esto sirve como feedback visual inmediato; en producción se filtraría por empresa/funcionario.
        try {
            // No hay un método directo de listar en el caso de uso, por eso usamos el repositorio.
            List<Visita> lista = AplicacionJavaFx.getContenedorDependencias().getVisitaRepositorio().listarTodos();
            visitas.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
