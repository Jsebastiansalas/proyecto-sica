package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.persona.BloquearPersonaComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Persona;
import com.acme.sica.dominio.puerto.entrada.GestionarBloqueoPersonaCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.DialogoConfirmacion;
import com.acme.sica.infraestructura.ui.javafx.Mensajes;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BloqueoPersonaControlador {

    @FXML private ComboBox<Persona> comboPersonas;
    @FXML private TextArea campoMotivo;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;
    @FXML private TableView<Persona> tablaBloqueadas;
    @FXML private TableColumn<Persona, String> columnaDocumento;
    @FXML private TableColumn<Persona, String> columnaNombre;
    @FXML private TableColumn<Persona, String> columnaMotivo;
    @FXML private TableColumn<Persona, String> columnaAccion;

    private GestionarBloqueoPersonaCasoUso casoUso;
    private final ObservableList<Persona> bloqueadas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarBloqueoPersonaCasoUso();
        comboPersonas.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Persona p) { return p == null ? "" : p.getDocumentoIdentidad() + " - " + p.getNombreCompleto(); }
            public Persona fromString(String s) { return null; }
        });
        columnaDocumento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDocumentoIdentidad()));
        columnaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        columnaMotivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMotivoBloqueo()));
        columnaAccion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isBloqueada() ? "Bloqueada" : "Disponible"));
        tablaBloqueadas.setItems(bloqueadas);
        cargarPersonas();
        cargarBloqueadas();
    }

    @FXML
    private void bloquear() {
        Persona persona = comboPersonas.getValue();
        if (persona == null) { mensaje("Seleccione una persona", false); return; }

        String nombre = persona.getNombreCompleto();
        String motivo = campoMotivo.getText();
        DialogoConfirmacion.confirmar(
                Mensajes.get("dialogo.confirmar.bloqueo.titulo"),
                "¿Bloquear a '" + nombre + "'?",
                Mensajes.get("dialogo.confirmar.bloqueo.contenido"),
                () -> ejecutarBloqueo(persona.getId(), motivo)
        );
    }

    private void ejecutarBloqueo(Long personaId, String motivo) {
        try {
            casoUso.bloquear(new BloquearPersonaComando(personaId, motivo));
            mensaje("Persona bloqueada correctamente", true);
            limpiar(); cargarPersonas(); cargarBloqueadas();
        } catch (IllegalArgumentException | IllegalStateException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) { mensaje(e.getMessage(), false); }
    }

    @FXML
    private void desbloquear() {
        Persona persona = tablaBloqueadas.getSelectionModel().getSelectedItem();
        if (persona == null) { mensaje("Seleccione una persona bloqueada", false); return; }

        DialogoConfirmacion.confirmar(
                Mensajes.get("dialogo.confirmar.desbloqueo.titulo"),
                "¿Desbloquear a '" + persona.getNombreCompleto() + "'?",
                Mensajes.get("dialogo.confirmar.desbloqueo.contenido"),
                () -> ejecutarDesbloqueo(persona.getId())
        );
    }

    private void ejecutarDesbloqueo(Long personaId) {
        try {
            casoUso.desbloquear(personaId);
            mensaje("Persona desbloqueada correctamente", true);
            cargarPersonas(); cargarBloqueadas();
        } catch (IllegalStateException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) { mensaje(e.getMessage(), false); }
    }

    private void cargarPersonas() { comboPersonas.getItems().setAll(casoUso.listarTodas().stream().filter(p -> !p.isBloqueada()).toList()); }
    private void cargarBloqueadas() { bloqueadas.setAll(casoUso.listarBloqueadas()); }
    private void limpiar() { comboPersonas.setValue(null); campoMotivo.clear(); }
    private void mensaje(String texto, boolean correcto) { etiquetaMensaje.setStyle("-fx-text-fill: " + (correcto ? "#34d399" : "#f43f5e") + ";"); etiquetaMensaje.setText(texto); }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(botonVolver);
    }
}
