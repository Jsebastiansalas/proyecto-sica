package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.rol.AsignarRolesUsuarioComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Rol;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.entrada.GestionarRolCasoUso;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador de la interfaz JavaFX para asignar roles.
 * Recibe eventos de la vista y delega la lógica a los casos de uso de aplicación.
 */
public class AsignarRolesControlador {

    @FXML
    private ComboBox<Usuario> comboUsuarios;

    @FXML
    private VBox contenedorRoles;

    @FXML
    private Label etiquetaMensaje;

    private GestionarRolCasoUso gestionarRolCasoUso;
    private UsuarioRepositorioPuerto usuarioRepositorio;

    /**
     * Inicializa el controlador y configura los componentes de la vista.
     */
    @FXML
    public void initialize() {
        gestionarRolCasoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarRolCasoUso();
        usuarioRepositorio = AplicacionJavaFx.getContenedorDependencias().getUsuarioRepositorio();

        comboUsuarios.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? "" : usuario.getUsername() + " - " + usuario.getNombreCompleto();
            }

            @Override
            public Usuario fromString(String texto) {
                return null;
            }
        });

        comboUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, nuevo) -> mostrarRoles(nuevo));
        cargarUsuarios();
    }

    @FXML
    private void guardarAsignacion() {
        etiquetaMensaje.setText("");
        Usuario usuario = comboUsuarios.getValue();

        if (usuario == null) {
            etiquetaMensaje.setText("Seleccione un usuario");
            return;
        }

        Set<Long> rolIds = new HashSet<>();
        contenedorRoles.getChildren().stream()
                .filter(CheckBox.class::isInstance)
                .map(CheckBox.class::cast)
                .filter(CheckBox::isSelected)
                .map(checkBox -> (Long) checkBox.getUserData())
                .forEach(rolIds::add);

        try {
            gestionarRolCasoUso.asignarRoles(new AsignarRolesUsuarioComando(usuario.getId(), rolIds));
            etiquetaMensaje.setText("Roles asignados correctamente");
            cargarUsuarios();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }

    private void cargarUsuarios() {
        try {
            comboUsuarios.getItems().setAll(usuarioRepositorio.listarTodos());
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void mostrarRoles(Usuario usuario) {
        contenedorRoles.getChildren().clear();
        if (usuario == null) {
            return;
        }

        Set<Long> rolesAsignados = usuario.getRoles().stream()
                .map(Rol::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<Rol> roles = gestionarRolCasoUso.listarTodos();
        roles.forEach(rol -> {
            CheckBox checkBox = new CheckBox(rol.getNombre());
            checkBox.setUserData(rol.getId());
            checkBox.setSelected(rolesAsignados.contains(rol.getId()));
            contenedorRoles.getChildren().add(checkBox);
        });
    }

}
