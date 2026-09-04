package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.permiso.CrearPermisoComando;
import com.acme.sica.aplicacion.permiso.EditarPermisoComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Permiso;
import com.acme.sica.dominio.puerto.entrada.GestionarPermisoCasoUso;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.DialogoConfirmacion;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

/**
 * Controlador de la interfaz JavaFX para un permiso.
 * Recibe eventos de la vista y delega la lógica a los casos de uso de aplicación.
 */
public class PermisoControlador {

    @FXML
    private TableView<Permiso> tablaPermisos;

    @FXML
    private TableColumn<Permiso, String> columnaCodigo;

    @FXML
    private TableColumn<Permiso, String> columnaDescripcion;

    @FXML
    private TextField campoCodigo;

    @FXML
    private TextField campoDescripcion;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonGuardar;

    @FXML
    private Button botonEliminar;

    @FXML
    private Button botonLimpiar;

    @FXML
    private Button botonVolver;

    private GestionarPermisoCasoUso casoUso;
    private ObservableList<Permiso> permisos;
    private Permiso permisoSeleccionado;

    /**
     * Inicializa el controlador y configura los componentes de la vista.
     */
    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarPermisoCasoUso();
        this.permisos = FXCollections.observableArrayList();

        columnaCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombre()));
        columnaDescripcion.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescripcion()));

        tablaPermisos.setItems(permisos);
        tablaPermisos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.permisoSeleccionado = nuevo;
            if (nuevo != null) {
                campoCodigo.setText(nuevo.getNombre());
                campoDescripcion.setText(nuevo.getDescripcion());
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarPermisos();
    }

    @FXML
    private void guardarPermiso() {
        etiquetaMensaje.setText("");

        String codigo = campoCodigo.getText().trim();
        String descripcion = campoDescripcion.getText().trim();

        if (codigo.isEmpty()) {
            etiquetaMensaje.setText("El código del permiso es obligatorio");
            return;
        }

        try {
            if (permisoSeleccionado == null) {
                CrearPermisoComando comando = new CrearPermisoComando(codigo, descripcion);
                casoUso.crear(comando);
                etiquetaMensaje.setText("Permiso creado correctamente");
            } else {
                EditarPermisoComando comando = new EditarPermisoComando(permisoSeleccionado.getId(), codigo, descripcion);
                casoUso.editar(comando);
                etiquetaMensaje.setText("Permiso actualizado correctamente");
            }
            limpiarFormulario();
            cargarPermisos();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void eliminarPermiso() {
        etiquetaMensaje.setText("");

        if (permisoSeleccionado == null) {
            etiquetaMensaje.setText("Seleccione un permiso para eliminar");
            return;
        }

        DialogoConfirmacion.confirmarEliminacion("el permiso '" + permisoSeleccionado.getNombre() + "'", () -> {
            try {
                casoUso.eliminar(permisoSeleccionado.getId());
                etiquetaMensaje.setText("Permiso eliminado correctamente");
                limpiarFormulario();
                cargarPermisos();
            } catch (PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
                etiquetaMensaje.setText(e.getMessage());
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        permisoSeleccionado = null;
        tablaPermisos.getSelectionModel().clearSelection();
        campoCodigo.clear();
        campoDescripcion.clear();
        botonGuardar.setText("Guardar");
        etiquetaMensaje.setText("");
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }

    private void cargarPermisos() {
        try {
            List<Permiso> lista = casoUso.listarTodos();
            permisos.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

}
