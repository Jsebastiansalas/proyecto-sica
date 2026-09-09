package com.acme.sica.infraestructura.ui.javafx.controlador;

import com.acme.sica.dominio.modelo.Vehiculo;
import com.acme.sica.dominio.modelo.Visita;
import com.acme.sica.dominio.modelo.enumerados.EstadoVisita;
import com.acme.sica.infraestructura.ui.javafx.AplicacionJavaFx;
import com.acme.sica.infraestructura.ui.javafx.NavegacionHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controlador de la interfaz JavaFX para el registro de vehículos y control de flota.
 * Permite registrar o actualizar vehículos del catálogo y ver cuáles están dentro
 * de la instalación en este momento.
 */
public class VehiculosControlador {

    @FXML
    private TableView<Vehiculo> tablaVehiculos;

    @FXML
    private TableColumn<Vehiculo, String> columnaPlaca;

    @FXML
    private TableColumn<Vehiculo, String> columnaMarca;

    @FXML
    private TableColumn<Vehiculo, String> columnaTipo;

    @FXML
    private TableColumn<Vehiculo, String> columnaEstado;

    @FXML
    private TextField campoPlaca;

    @FXML
    private TextField campoMarca;

    @FXML
    private TextField campoTipo;

    @FXML
    private TextField campoBuscar;

    @FXML
    private Label etiquetaMensaje;

    @FXML
    private Button botonGuardar;

    @FXML
    private Button botonLimpiar;

    @FXML
    private Button botonVolver;

    private ObservableList<Vehiculo> vehiculos;
    private List<Vehiculo> todos;
    private Set<String> placasDentro;
    private Vehiculo vehiculoSeleccionado;

    @FXML
    public void initialize() {
        var contenedor = AplicacionJavaFx.getContenedorDependencias();
        this.vehiculos = FXCollections.observableArrayList();
        this.placasDentro = Set.of();

        columnaPlaca.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPlaca()));
        columnaMarca.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMarca() != null ? c.getValue().getMarca() : ""));
        columnaTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo() != null ? c.getValue().getTipo() : ""));
        columnaEstado.setCellValueFactory(c -> new SimpleStringProperty(
                placasDentro.contains(c.getValue().getPlaca()) ? "Dentro" : "Fuera"));

        tablaVehiculos.setItems(vehiculos);
        tablaVehiculos.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nuevo) -> {
            this.vehiculoSeleccionado = nuevo;
            if (nuevo != null) {
                campoPlaca.setText(nuevo.getPlaca());
                campoMarca.setText(nuevo.getMarca());
                campoTipo.setText(nuevo.getTipo());
                botonGuardar.setText("Actualizar");
            } else {
                limpiarFormulario();
            }
        });

        cargarVehiculos();
    }

    @FXML
    private void guardarVehiculo() {
        etiquetaMensaje.setText("");

        String placa = campoPlaca.getText().trim().toUpperCase();
        String marca = campoMarca.getText().trim();
        String tipo = campoTipo.getText() != null ? campoTipo.getText().trim() : "";

        if (placa.isEmpty()) {
            etiquetaMensaje.setText("La placa es obligatoria");
            return;
        }

        try {
            var contenedor = AplicacionJavaFx.getContenedorDependencias();
            contenedor.getVehiculoRepositorio().guardar(new Vehiculo(placa, marca, tipo));
            etiquetaMensaje.setText("Vehículo " + (vehiculoSeleccionado != null ? "actualizado" : "registrado") + " correctamente");
            limpiarFormulario();
            cargarVehiculos();
        } catch (RuntimeException e) {
            etiquetaMensaje.setText("Error al guardar el vehículo: " + e.getMessage());
        }
    }

    @FXML
    private void limpiarFormulario() {
        vehiculoSeleccionado = null;
        tablaVehiculos.getSelectionModel().clearSelection();
        campoPlaca.clear();
        campoMarca.clear();
        campoTipo.clear();
        botonGuardar.setText("Guardar");
        etiquetaMensaje.setText("");
    }

    @FXML
    private void filtrarVehiculos() {
        String filtro = campoBuscar.getText() == null ? "" : campoBuscar.getText().trim().toLowerCase();
        if (filtro.isEmpty()) {
            vehiculos.setAll(todos);
            return;
        }
        vehiculos.setAll(todos.stream()
                .filter(v -> contiene(v.getPlaca(), filtro)
                        || contiene(v.getMarca(), filtro)
                        || contiene(v.getTipo(), filtro))
                .toList());
    }

    @FXML
    private void volverAlDashboard() {
        NavegacionHelper.volverAlDashboard(etiquetaMensaje);
    }

    private void cargarVehiculos() {
        try {
            var contenedor = AplicacionJavaFx.getContenedorDependencias();
            this.placasDentro = contenedor.getVisitaRepositorio().listarTodos().stream()
                    .filter(v -> v.getEstado() == EstadoVisita.DENTRO && v.getVehiculo() != null)
                    .map(v -> v.getVehiculo().getPlaca())
                    .collect(Collectors.toSet());
            this.todos = contenedor.getVehiculoRepositorio().listarTodos();
            filtrarVehiculos();
        } catch (RuntimeException e) {
            etiquetaMensaje.setText("No se pudo cargar el listado de vehículos");
        }
    }

    private boolean contiene(String valor, String filtro) {
        return valor != null && valor.toLowerCase().contains(filtro);
    }
}