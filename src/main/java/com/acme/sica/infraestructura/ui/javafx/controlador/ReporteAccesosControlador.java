package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.reporte.ConsultarReporteAccesosComando;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.puerto.entrada.ConsultarReporteAccesosCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReporteAccesosControlador {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    @FXML private DatePicker campoDesde;
    @FXML private DatePicker campoHasta;
    @FXML private ComboBox<Empresa> comboEmpresa;
    @FXML private TableView<Visita> tabla;
    @FXML private TableColumn<Visita,String> columnaPersona, columnaTipo, columnaIngreso, columnaSalida, columnaEstado;
    @FXML private Label etiquetaMensaje;
    @FXML private Button botonVolver;
    private ConsultarReporteAccesosCasoUso casoUso;

    @FXML public void initialize() {
        var contenedor = AplicacionJavaFx.getContenedorDependencias();
        casoUso = contenedor.getConsultarReporteAccesosCasoUso();
        comboEmpresa.setConverter(new StringConverter<>() { public String toString(Empresa e){return e == null ? "Todas" : e.getNombre();} public Empresa fromString(String s){return null;} });
        comboEmpresa.getItems().add(null);
        comboEmpresa.getItems().addAll(contenedor.getEmpresaRepositorio().listarTodos());
        columnaPersona.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersona().getNombreCompleto()));
        columnaTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersona().getTipo().name()));
        columnaIngreso.setCellValueFactory(c -> fecha(c.getValue().getFechaHoraIngreso()));
        columnaSalida.setCellValueFactory(c -> fecha(c.getValue().getFechaHoraSalida()));
        columnaEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().name()));
        buscar();
    }

    @FXML private void buscar() {
        LocalDateTime desde = campoDesde.getValue() == null ? null : campoDesde.getValue().atStartOfDay();
        LocalDateTime hasta = campoHasta.getValue() == null ? null : campoHasta.getValue().atTime(23,59,59);
        try { tabla.setItems(FXCollections.observableArrayList(casoUso.consultar(new ConsultarReporteAccesosComando(desde, hasta, comboEmpresa.getValue() == null ? null : comboEmpresa.getValue().getId())))); etiquetaMensaje.setText("Reporte actualizado"); }
        catch (RuntimeException e) { etiquetaMensaje.setText(e.getMessage()); }
    }
    @FXML private void limpiar() { campoDesde.setValue(null); campoHasta.setValue(null); comboEmpresa.setValue(null); buscar(); }
    private SimpleStringProperty fecha(LocalDateTime fecha) { return new SimpleStringProperty(fecha == null ? "-" : fecha.format(FORMATO)); }
    @FXML private void volverAlDashboard() { try { Parent p=FXMLLoader.load(getClass().getResource("/fxml/dashboard.fxml")); Scene s=new Scene(p); s.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm()); Stage st=(Stage)botonVolver.getScene().getWindow(); st.setScene(s); st.setTitle("SICA - Dashboard"); st.show(); } catch(IOException e){throw new RuntimeException("Error al volver al dashboard",e);} }
}
