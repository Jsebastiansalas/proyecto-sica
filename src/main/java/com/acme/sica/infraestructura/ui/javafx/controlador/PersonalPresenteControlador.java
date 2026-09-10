package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.TipoPersona;
import com.acme.sica.dominio.puerto.salida.VisitaRepositorioPuerto;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Optional;

public class PersonalPresenteControlador {

    @FXML
    private TableView<Visita> tablaPersonal;

    @FXML
    private TableColumn<Visita, String> colDocumentoVisitante;

    @FXML
    private TableColumn<Visita, String> colNombreVisitante;

    @FXML
    private TableColumn<Visita, String> colTipoPersona;

    @FXML
    private TableColumn<Visita, String> colHoraIngreso;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    public void initialize() {
        colDocumentoVisitante.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersona().getDocumentoIdentidad()));
        colNombreVisitante.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersona().getNombreCompleto()));
        colTipoPersona.setCellValueFactory(c -> new SimpleStringProperty(tipo(c.getValue())));
        colHoraIngreso.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaHoraIngreso().toString()));
        consultar();
    }

    private void consultar() {
        VisitaRepositorioPuerto repositorio = AplicacionJavaFx.getContenedorDependencias().getVisitaRepositorio();
        Optional<Funcionario> funcionario = AplicacionJavaFx.getContenedorDependencias().getFuncionarioRepositorio()
                .buscarPorUsuario(SesionContexto.obtener().get().getUsuarioId());
        Long empresaId = funcionario.get().getEmpresa().getId();
        List<Visita> visitas = repositorio.consultarPersonalPresentePorEmpresa(empresaId);

        tablaPersonal.setItems(FXCollections.observableArrayList(visitas));
        if (visitas.isEmpty()) {
            etiquetaMensaje.setText("No hay personas de su empresa dentro del complejo");
            return;
        }

        System.out.println("=== PERSONAL PRESENTE EN EL COMPLEJO ===");
        for (Visita v : visitas) {
            System.out.printf("%s | %s | %s | %s%n",
                    v.getPersona().getNombreCompleto(),
                    v.getPersona().getDocumentoIdentidad(),
                    tipo(v),
                    v.getFechaHoraIngreso());
        }
    }

    private String tipo(Visita v) {
        return v.getPersona().getTipo() == TipoPersona.TRABAJADOR ? "Trabajador" : "Invitado";
    }

    @FXML
    public void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }
}