package pe.edu.upeu.project_final_gp1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.project_final_gp1.modelo.Habitacion;

public class HbitacionFromController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNumero;
    @FXML private TextField txtNumeroPiso;
    @FXML private TextField txtNumeroHabitacion;
    @FXML private TextField txtPrecio;
    @FXML private ComboBox<String> cbTipo;
    @FXML private ComboBox<String> cbEstado;

    private Habitacion habitacion;
    private boolean saved = false;

    @FXML
    public void initialize() {
        if (cbTipo != null && cbTipo.getItems().isEmpty()) {
            cbTipo.getItems().setAll("Simple", "Doble", "Matrimonial", "Familiar", "Suite");
        }
        if (cbEstado != null && cbEstado.getItems().isEmpty()) {
            cbEstado.getItems().setAll("Disponible", "Ocupada", "Mantenimiento");
        }
    }

    public void setHabitacion(Habitacion h) {
        this.habitacion = h;
        if (h != null) {
            lblTitulo.setText("Editar habitación");
            txtNumero.setText(h.getNumero());
            txtNumeroPiso.setText(h.getNumeroPiso());
            txtNumeroHabitacion.setText(h.getNumeroHabitacion());
            txtPrecio.setText(String.valueOf(h.getPrecio()));
            cbTipo.setValue(h.getTipo());
            cbEstado.setValue(h.getEstado());
        } else {
            lblTitulo.setText("Nueva habitación");
            cbEstado.setValue("Disponible");
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    private boolean validated() {
        if (txtNumero.getText().trim().isEmpty()
                || txtNumeroPiso.getText().trim().isEmpty()
                || txtNumeroHabitacion.getText().trim().isEmpty()
                || txtPrecio.getText().trim().isEmpty()
                || cbTipo.getValue() == null
                || cbEstado.getValue() == null) {
            mostrarAviso("Completa todos los campos obligatorios.");
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (!Double.isFinite(precio) || precio < 0) {
                mostrarAviso("El precio debe ser un número válido y no negativo.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAviso("El precio debe ser numérico. Ejemplo: 120.00");
            return false;
        }
        return true;
    }

    @FXML
    private void handleCancel() {
        cerrar();
    }

    @FXML
    private void handleSaved() {
        if (!validated()) return;

        if (habitacion == null) {
            habitacion = new Habitacion();
        }

        habitacion.setNumero(txtNumero.getText().trim());
        habitacion.setNumeroPiso(txtNumeroPiso.getText().trim());
        habitacion.setNumeroHabitacion(txtNumeroHabitacion.getText().trim());
        habitacion.setTipo(cbTipo.getValue());
        habitacion.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        habitacion.setEstado(cbEstado.getValue());

        saved = true;
        cerrar();
    }

    private void cerrar() {
        if (txtNumero != null && txtNumero.getScene() != null) {
            Stage stage = (Stage) txtNumero.getScene().getWindow();
            stage.close();
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Formulario de habitación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
