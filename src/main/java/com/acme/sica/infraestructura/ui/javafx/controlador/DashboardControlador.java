package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.autenticacion.SesionContexto;
import com.acme.sica.dominio.modelo.BitacoraAuditoria;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.dominio.puerto.salida.*;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardControlador {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy — HH:mm:ss", new Locale("es"));
    private static final DateTimeFormatter FORMATO_ACTIVIDAD =
            DateTimeFormatter.ofPattern("HH:mm", new Locale("es"));

    @FXML private Label etiquetaBienvenida;
    @FXML private Label etiquetaRol;
    @FXML private Label etiquetaFecha;

    @FXML private Label statUsuarios;
    @FXML private Label statRoles;
    @FXML private Label statEmpresas;
    @FXML private Label statPersonas;
    @FXML private Label statVisitasPendientes;

    @FXML private VBox contenedorActividadReciente;

    @FXML private Button botonGestionarRoles;
    @FXML private Button botonGestionarPermisos;
    @FXML private Button botonAsignarRoles;
    @FXML private Button botonConsultarBitacora;
    @FXML private Button botonGestionarEmpresas;
    @FXML private Button botonGestionarFuncionarios;
    @FXML private Button botonGestionarPersonas;
    @FXML private Button botonPreRegistrarInvitado;
    @FXML private Button botonCheckIn;
    @FXML private Button botonRegistrarNoAnunciado;
    @FXML private Button botonAprobarRechazar;
    @FXML private Button botonRegistrarTrabajador;
    @FXML private Button botonCerrarSesion;

    @FXML
    public void initialize() {
        SesionContexto.obtener().ifPresent(sesion -> {
            etiquetaBienvenida.setText("Bienvenido, " + sesion.getNombreCompleto());
            etiquetaRol.setText("@" + sesion.getUsername());
        });

        botonGestionarRoles.setVisible(SesionContexto.tienePermiso("gestionar_roles"));
        botonGestionarPermisos.setVisible(SesionContexto.tienePermiso("gestionar_permisos"));
        botonAsignarRoles.setVisible(SesionContexto.tienePermiso("asignar_roles"));
        botonConsultarBitacora.setVisible(SesionContexto.tienePermiso("consultar_bitacora"));
        botonGestionarEmpresas.setVisible(SesionContexto.tienePermiso("gestionar_empresas"));
        botonGestionarFuncionarios.setVisible(SesionContexto.tienePermiso("gestionar_funcionarios"));
        botonGestionarPersonas.setVisible(SesionContexto.tienePermiso("registrar_persona"));
        botonPreRegistrarInvitado.setVisible(SesionContexto.tienePermiso("pre_registrar_invitado"));
        botonCheckIn.setVisible(SesionContexto.tienePermiso("check_in_invitado"));
        botonRegistrarNoAnunciado.setVisible(SesionContexto.tienePermiso("registrar_no_anunciado"));
        botonAprobarRechazar.setVisible(SesionContexto.tienePermiso("aprobar_rechazar"));
        botonRegistrarTrabajador.setVisible(SesionContexto.tienePermiso("registrar_trabajador"));

        cargarEstadisticas();
        cargarActividadReciente();
        iniciarReloj();
    }

    private void cargarEstadisticas() {
        try {
            var contenedor = AplicacionJavaFx.getContenedorDependencias();
            statUsuarios.setText(String.valueOf(contenedor.getUsuarioRepositorio().listarTodos().size()));
            statRoles.setText(String.valueOf(contenedor.getRolRepositorio().listarTodos().size()));
            statEmpresas.setText(String.valueOf(contenedor.getEmpresaRepositorio().listarTodos().size()));
            statPersonas.setText(String.valueOf(contenedor.getPersonaRepositorio().listarTodos().size()));

            long pendientes = contenedor.getVisitaRepositorio().listarTodos().stream()
                    .filter(v -> v.getEstado() == EstadoVisita.PENDIENTE_APROBACION
                            || v.getEstado() == EstadoVisita.PENDIENTE_APROBACION_OLVIDO)
                    .count();
            statVisitasPendientes.setText(String.valueOf(pendientes));
        } catch (Exception e) {
            statUsuarios.setText("—");
            statRoles.setText("—");
            statEmpresas.setText("—");
            statPersonas.setText("—");
            statVisitasPendientes.setText("—");
        }
    }

    private void cargarActividadReciente() {
        contenedorActividadReciente.getChildren().clear();
        try {
            BitacoraRepositorioPuerto bitacoraRepo =
                    AplicacionJavaFx.getContenedorDependencias().getBitacoraRepositorio();
            List<BitacoraAuditoria> registros = bitacoraRepo.listarTodos();
            int limite = Math.min(7, registros.size());

            for (int i = 0; i < limite; i++) {
                contenedorActividadReciente.getChildren().add(crearFilaActividad(registros.get(i)));
            }

            if (registros.isEmpty()) {
                Label vacio = new Label("Sin actividad reciente");
                vacio.getStyleClass().add("activity-empty");
                contenedorActividadReciente.getChildren().add(vacio);
            }
        } catch (Exception e) {
            Label error = new Label("No se pudo cargar la actividad");
            error.getStyleClass().add("activity-empty");
            contenedorActividadReciente.getChildren().add(error);
        }
    }

    private HBox crearFilaActividad(BitacoraAuditoria registro) {
        HBox fila = new HBox();
        fila.getStyleClass().add("activity-row");
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setSpacing(12);

        Label hora = new Label(registro.getFechaHora() != null
                ? registro.getFechaHora().format(FORMATO_ACTIVIDAD) : "--:--");
        hora.getStyleClass().add("activity-time");

        Label usuario = new Label(registro.getUsuarioNombre() != null
                ? registro.getUsuarioNombre() : "sistema");
        usuario.getStyleClass().add("activity-user");

        Label accion = new Label(registro.getAccion() != null ? registro.getAccion() : "");
        accion.getStyleClass().add("activity-action");
        accion.setWrapText(true);

        fila.getChildren().addAll(hora, usuario, accion);
        return fila;
    }

    private void iniciarReloj() {
        etiquetaFecha.setText(LocalDateTime.now().format(FORMATO_FECHA));
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e ->
                etiquetaFecha.setText(LocalDateTime.now().format(FORMATO_FECHA))));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML private void abrirGestionRoles() { cargarVista("/fxml/roles.fxml", "SICA - Gestión de Roles"); }
    @FXML private void abrirGestionPermisos() { cargarVista("/fxml/permisos.fxml", "SICA - Gestión de Permisos"); }
    @FXML private void abrirAsignacionRoles() { cargarVista("/fxml/asignar-roles.fxml", "SICA - Asignar Roles"); }
    @FXML private void abrirConsultarBitacora() { cargarVista("/fxml/bitacora.fxml", "SICA - Bitácora de Auditoría"); }
    @FXML private void abrirGestionEmpresas() { cargarVista("/fxml/empresas.fxml", "SICA - Gestión de Empresas"); }
    @FXML private void abrirGestionFuncionarios() { cargarVista("/fxml/funcionarios.fxml", "SICA - Gestión de Funcionarios"); }
    @FXML private void abrirGestionPersonas() { cargarVista("/fxml/personas.fxml", "SICA - Gestión de Personas"); }
    @FXML private void abrirPreRegistrarInvitado() { cargarVista("/fxml/pre-registrar-invitado.fxml", "SICA - Pre-registrar Invitado"); }
    @FXML private void abrirCheckIn() { cargarVista("/fxml/check-in.fxml", "SICA - Check-in de Invitados"); }
    @FXML private void abrirRegistrarNoAnunciado() { cargarVista("/fxml/registrar-no-anunciado.fxml", "SICA - Registrar No Anunciado"); }
    @FXML private void abrirAprobarRechazar() { cargarVista("/fxml/aprobar-rechazar.fxml", "SICA - Aprobar/Rechazar Visitas"); }
    @FXML private void abrirRegistrarTrabajador() { cargarVista("/fxml/registrar-trabajador.fxml", "SICA - Registrar Trabajador"); }

    @FXML
    private void cerrarSesion() {
        SesionContexto.cerrar();
        cargarVista("/fxml/login.fxml", "SICA - Zona Acme");
    }

    private void cargarVista(String rutaFxml, String titulo) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource(rutaFxml));
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

            Stage stage = (Stage) etiquetaBienvenida.getScene().getWindow();
            stage.setTitle(titulo);
            stage.setScene(escena);
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la vista: " + rutaFxml, e);
        }
    }
}
