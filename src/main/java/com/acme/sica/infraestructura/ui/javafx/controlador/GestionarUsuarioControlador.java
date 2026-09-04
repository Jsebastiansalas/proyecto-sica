package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.usuario.CrearUsuarioComando;
import com.acme.sica.aplicacion.usuario.EditarUsuarioComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.entrada.GestionarUsuarioCasoUso;
import com.acme.sica.dominio.puerto.salida.RolRepositorioPuerto;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.DialogoConfirmacion;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador de la interfaz JavaFX para gestionar usuarios.
 * Recibe eventos de la vista y delega la lógica a los casos de uso de aplicación.
 */
public class GestionarUsuarioControlador {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> columnaUsername;
    @FXML private TableColumn<Usuario, String> columnaNombre;
    @FXML private TableColumn<Usuario, String> columnaRoles;
    @FXML private TableColumn<Usuario, String> columnaEstado;

    @FXML private TextField campoUsername;
    @FXML private TextField campoNombre;
    @FXML private PasswordField campoPassword;
    @FXML private CheckBox checkActivo;
    @FXML private ListView<Rol> listaRoles;

    @FXML private Label etiquetaMensaje;
    @FXML private Button botonGuardar;
    @FXML private Button botonEliminar;
    @FXML private Button botonLimpiar;
    @FXML private Button botonVolver;

    private GestionarUsuarioCasoUso casoUso;
    private RolRepositorioPuerto rolRepositorio;
    private ObservableList<Usuario> usuarios;
    private ObservableList<Rol> rolesDisponibles;
    private Usuario usuarioSeleccionado;

    /**
     * Inicializa el controlador y configura los componentes de la vista.
     */
    @FXML
    public void initialize() {
        this.casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarUsuarioCasoUso();
        this.rolRepositorio = AplicacionJavaFx.getContenedorDependencias().getRolRepositorio();
        this.usuarios = FXCollections.observableArrayList();
        this.rolesDisponibles = FXCollections.observableArrayList();

        listaRoles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        columnaUsername.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUsername()));
        columnaNombre.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNombreCompleto()));
        columnaRoles.setCellValueFactory(cell -> {
            Set<String> roles = new HashSet<>();
            if (cell.getValue().getRoles() != null) {
                cell.getValue().getRoles().forEach(r -> roles.add(r.getNombre()));
            }
            return new SimpleStringProperty(String.join(", ", roles));
        });
        columnaEstado.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().isActivo() ? "Activo" : "Inactivo"));

        tablaUsuarios.setItems(usuarios);
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.usuarioSeleccionado = nuevo;
            if (nuevo != null) {
                campoUsername.setText(nuevo.getUsername());
                campoNombre.setText(nuevo.getNombreCompleto());
                campoPassword.clear();
                checkActivo.setSelected(nuevo.isActivo());
                seleccionarRoles(nuevo);
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarRoles();
        cargarUsuarios();
    }

    @FXML
    private void guardarUsuario() {
        etiquetaMensaje.setText("");
        etiquetaMensaje.setStyle("-fx-text-fill: #f43f5e;");

        String username = campoUsername.getText().trim();
        String nombre = campoNombre.getText().trim();
        String password = campoPassword.getText();
        boolean activo = checkActivo.isSelected();

        if (username.isEmpty()) {
            etiquetaMensaje.setText("El username es obligatorio");
            return;
        }
        if (nombre.isEmpty()) {
            etiquetaMensaje.setText("El nombre es obligatorio");
            return;
        }

        Set<Long> rolIds = new HashSet<>();
        for (Rol rol : listaRoles.getSelectionModel().getSelectedItems()) {
            rolIds.add(rol.getId());
        }

        try {
            if (usuarioSeleccionado == null) {
                if (password.isEmpty()) {
                    etiquetaMensaje.setText("La contraseña es obligatoria para nuevos usuarios");
                    return;
                }
                casoUso.crear(new CrearUsuarioComando(username, password, nombre, rolIds));
                etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
                etiquetaMensaje.setText("Usuario creado correctamente");
            } else {
                casoUso.editar(new EditarUsuarioComando(
                        usuarioSeleccionado.getId(), username, nombre, password, activo, rolIds));
                etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
                etiquetaMensaje.setText("Usuario actualizado correctamente");
            }
            limpiarFormulario();
            cargarUsuarios();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion
                 | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void eliminarUsuario() {
        etiquetaMensaje.setText("");

        if (usuarioSeleccionado == null) {
            etiquetaMensaje.setText("Seleccione un usuario para eliminar");
            return;
        }

        DialogoConfirmacion.confirmarEliminacion("el usuario '" + usuarioSeleccionado.getUsername() + "'", () -> {
            try {
                casoUso.eliminar(usuarioSeleccionado.getId());
                etiquetaMensaje.setStyle("-fx-text-fill: #34d399;");
                etiquetaMensaje.setText("Usuario eliminado correctamente");
                limpiarFormulario();
                cargarUsuarios();
            } catch (PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion
                     | IllegalArgumentException e) {
                etiquetaMensaje.setText(e.getMessage());
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        usuarioSeleccionado = null;
        tablaUsuarios.getSelectionModel().clearSelection();
        campoUsername.clear();
        campoNombre.clear();
        campoPassword.clear();
        checkActivo.setSelected(true);
        listaRoles.getSelectionModel().clearSelection();
        botonGuardar.setText("Guardar");
        etiquetaMensaje.setText("");
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(botonVolver);
    }

    private void cargarRoles() {
        try {
            List<Rol> roles = rolRepositorio.listarTodos().stream()
                    .filter(Rol::isActivo)
                    .toList();
            rolesDisponibles.setAll(roles);
            listaRoles.setItems(rolesDisponibles);
        } catch (Exception e) {
            etiquetaMensaje.setText("Error al cargar roles: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> lista = casoUso.listarTodos();
            usuarios.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void seleccionarRoles(Usuario usuario) {
        listaRoles.getSelectionModel().clearSelection();
        if (usuario.getRoles() != null) {
            for (Rol rolUsuario : usuario.getRoles()) {
                for (Rol rolDisponible : rolesDisponibles) {
                    if (rolDisponible.getId().equals(rolUsuario.getId())) {
                        listaRoles.getSelectionModel().select(rolDisponible);
                        break;
                    }
                }
            }
        }
    }
}