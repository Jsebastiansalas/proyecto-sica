package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.reporte.ConsultarReporteIncidentesComando;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Incidente;
import com.acme.sica.dominio.modelo.enumerados.GravedadIncidente;
import com.acme.sica.dominio.puerto.entrada.ConsultarReporteIncidentesCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.IndicadorCarga;
import com.acme.sica.infraestructura.ui.javafx.Mensajes;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controlador de la interfaz JavaFX para reporte incidentes.
 * Recibe eventos de la vista y delega la lógica a los casos de uso de aplicación.
 */
public class ReporteIncidentesControlador {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @FXML private DatePicker campoDesde, campoHasta;
    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private ComboBox<GravedadIncidente> comboGravedad;
    @FXML private TableView<Incidente> tabla;
    @FXML private TableColumn<Incidente,String> columnaFecha, columnaPersona, columnaGravedad, columnaDescripcion, columnaUsuario;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;
    private ConsultarReporteIncidentesCasoUso casoUso;

    @FXML public void initialize() {
        var contenedor = AplicacionJavaFx.getContenedorDependencias(); casoUso=contenedor.getConsultarReporteIncidentesCasoUso();
        comboEmpresa.setConverter(new StringConverter<>() { public String toString(Empresa e){return e==null?"Todas":e.getNombre();} public Empresa fromString(String s){return null;} }); comboEmpresa.getItems().add(null); comboEmpresa.getItems().addAll(contenedor.getEmpresaRepositorio().listarTodos());
        comboGravedad.getItems().add(null); comboGravedad.getItems().addAll(GravedadIncidente.values()); comboGravedad.setConverter(new StringConverter<>() {public String toString(GravedadIncidente g){return g==null?"Todas":g.name();} public GravedadIncidente fromString(String s){return null;}});
        columnaFecha.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getFechaCreacion().format(FORMATO))); columnaPersona.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getPersona().getNombreCompleto())); columnaGravedad.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getGravedad().name())); columnaDescripcion.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getDescripcion())); columnaUsuario.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getRegistradoPor().getUsername())); buscar();
    }
    @FXML private void buscar() {
        IndicadorCarga.ejecutar(etiquetaMensaje, Mensajes.get("carga.generando_reporte"), () -> {
            LocalDateTime d = campoDesde.getValue() == null ? null : campoDesde.getValue().atStartOfDay();
            LocalDateTime h = campoHasta.getValue() == null ? null : campoHasta.getValue().atTime(23, 59, 59);
            try { tabla.setItems(FXCollections.observableArrayList(casoUso.consultar(new ConsultarReporteIncidentesComando(d, h, comboEmpresa.getValue() == null ? null : comboEmpresa.getValue().getId(), comboGravedad.getValue())))); etiquetaMensaje.setText("Reporte actualizado"); }
            catch (RuntimeException e) { etiquetaMensaje.setText(e.getMessage()); }
        });
    }
    @FXML private void limpiar(){campoDesde.setValue(null);campoHasta.setValue(null);comboEmpresa.setValue(null);comboGravedad.setValue(null);buscar();}
    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(botonVolver);
    }
}
