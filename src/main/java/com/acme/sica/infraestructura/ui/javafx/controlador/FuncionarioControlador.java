package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.aplicacion.funcionario.CrearFuncionarioComando;
import com.acme.sica.aplicacion.funcionario.EditarFuncionarioComando;
import com.acme.sica.dominio.excepciones.EntidadNoEncontradaExcepcion;
import com.acme.sica.dominio.excepciones.PermisoDenegadoExcepcion;
import com.acme.sica.dominio.modelo.Empresa;
import com.acme.sica.dominio.modelo.Funcionario;
import com.acme.sica.dominio.modelo.Usuario;
import com.acme.sica.dominio.puerto.entrada.GestionarFuncionarioCasoUso;
import com.acme.sica.dominio.puerto.salida.EmpresaRepositorioPuerto;
import com.acme.sica.dominio.puerto.salida.UsuarioRepositorioPuerto;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.DialogoConfirmacion;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.util.List;

public class FuncionarioControlador {

    private static final String SIN_USUARIO = "— Sin usuario —";

    @FXML
    private TableView<Funcionario> tablaFuncionarios;

    @FXML
    private TableColumn<Funcionario, String> columnaNombre;

    @FXML
    private TableColumn<Funcionario, String> columnaCargo;

    @FXML
    private TableColumn<Funcionario, String> columnaEmpresa;

    @FXML
    private TableColumn<Funcionario, String> columnaUsuario;

    @FXML
    private TableColumn<Funcionario, String> columnaEstado;

    @FXML
    private ComboBox<Empresa> comboEmpresas;

    @FXML
    private ComboBox<Usuario> comboUsuarios;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoCargo;

    @FXML
    private CheckBox checkActivo;

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

    private GestionarFuncionarioCasoUso casoUso;
    private EmpresaRepositorioPuerto empresaRepositorio;
    private UsuarioRepositorioPuerto usuarioRepositorio;
    private ObservableList<Funcionario> funcionarios;
    private Funcionario funcionarioSeleccionado;

    @FXML
    public void initialize() {
        casoUso = AplicacionJavaFx.getContenedorDependencias().getGestionarFuncionarioCasoUso();
        empresaRepositorio = AplicacionJavaFx.getContenedorDependencias().getEmpresaRepositorio();
        usuarioRepositorio = AplicacionJavaFx.getContenedorDependencias().getUsuarioRepositorio();
        funcionarios = FXCollections.observableArrayList();

        configurarCombos();
        configurarColumnas();

        tablaFuncionarios.setItems(funcionarios);
        tablaFuncionarios.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.funcionarioSeleccionado = nuevo;
            if (nuevo != null) {
                campoNombre.setText(nuevo.getNombreCompleto());
                campoCargo.setText(nuevo.getCargo());
                checkActivo.setSelected(nuevo.isActivo());
                seleccionarEmpresa(nuevo.getEmpresa());
                seleccionarUsuario(nuevo.getUsuario());
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarEmpresas();
        cargarUsuarios();
        cargarFuncionarios();
    }

    private void configurarCombos() {
        comboEmpresas.setConverter(new StringConverter<>() {
            @Override
            public String toString(Empresa empresa) {
                return empresa == null ? "" : empresa.getNombre();
            }

            @Override
            public Empresa fromString(String texto) {
                return null;
            }
        });

        comboUsuarios.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario == null ? SIN_USUARIO
                        : usuario.getUsername() + " - " + usuario.getNombreCompleto();
            }

            @Override
            public Usuario fromString(String texto) {
                return null;
            }
        });
    }

    private void configurarColumnas() {
        columnaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        columnaCargo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCargo()));
        columnaEmpresa.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEmpresa() != null ? c.getValue().getEmpresa().getNombre() : ""));
        columnaUsuario.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getUsuario() != null ? c.getValue().getUsuario().getUsername() : ""));
        columnaEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isActivo() ? "Activo" : "Inactivo"));
    }

    @FXML
    private void guardarFuncionario() {
        etiquetaMensaje.setText("");

        Empresa empresa = comboEmpresas.getValue();
        if (empresa == null) {
            etiquetaMensaje.setText("Seleccione una empresa");
            return;
        }

        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            etiquetaMensaje.setText("El nombre del funcionario es obligatorio");
            return;
        }

        String cargo = campoCargo.getText().trim();
        Usuario usuario = comboUsuarios.getValue();

        try {
            if (funcionarioSeleccionado == null) {
                casoUso.crear(new CrearFuncionarioComando(
                        usuarioId(usuario), empresa.getId(), nombre, cargo));
                etiquetaMensaje.setText("Funcionario creado correctamente");
            } else {
                casoUso.editar(new EditarFuncionarioComando(
                        funcionarioSeleccionado.getId(),
                        usuarioId(usuario), empresa.getId(), nombre, cargo, checkActivo.isSelected()));
                etiquetaMensaje.setText("Funcionario actualizado correctamente");
            }
            limpiarFormulario();
            cargarFuncionarios();
        } catch (IllegalArgumentException | PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void eliminarFuncionario() {
        etiquetaMensaje.setText("");

        if (funcionarioSeleccionado == null) {
            etiquetaMensaje.setText("Seleccione un funcionario para eliminar");
            return;
        }

        DialogoConfirmacion.confirmarEliminacion("el funcionario '" + funcionarioSeleccionado.getNombreCompleto() + "'", () -> {
            try {
                casoUso.eliminar(funcionarioSeleccionado.getId());
                etiquetaMensaje.setText("Funcionario eliminado correctamente");
                limpiarFormulario();
                cargarFuncionarios();
            } catch (PermisoDenegadoExcepcion | EntidadNoEncontradaExcepcion e) {
                etiquetaMensaje.setText(e.getMessage());
            }
        });
    }

    @FXML
    private void limpiarFormulario() {
        funcionarioSeleccionado = null;
        tablaFuncionarios.getSelectionModel().clearSelection();
        campoNombre.clear();
        campoCargo.clear();
        checkActivo.setSelected(true);
        comboEmpresas.getSelectionModel().clearSelection();
        comboUsuarios.getSelectionModel().clearSelection();
        botonGuardar.setText("Guardar");
        etiquetaMensaje.setText("");
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }

    private void cargarEmpresas() {
        try {
            comboEmpresas.getItems().setAll(empresaRepositorio.listarTodos());
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void cargarUsuarios() {
        try {
            comboUsuarios.getItems().setAll(usuarioRepositorio.listarTodos());
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void cargarFuncionarios() {
        try {
            List<Funcionario> lista = casoUso.listarTodos();
            funcionarios.setAll(lista);
        } catch (PermisoDenegadoExcepcion e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    private void seleccionarEmpresa(Empresa empresa) {
        if (empresa == null) {
            return;
        }
        comboEmpresas.getItems().stream()
                .filter(e -> e.getId().equals(empresa.getId()))
                .findFirst()
                .ifPresent(comboEmpresas.getSelectionModel()::select);
    }

    private void seleccionarUsuario(Usuario usuario) {
        if (usuario == null) {
            comboUsuarios.getSelectionModel().clearSelection();
            return;
        }
        comboUsuarios.getItems().stream()
                .filter(u -> u.getId().equals(usuario.getId()))
                .findFirst()
                .ifPresent(comboUsuarios.getSelectionModel()::select);
    }

    private Long usuarioId(Usuario usuario) {
        return usuario == null ? null : usuario.getId();
    }
}
