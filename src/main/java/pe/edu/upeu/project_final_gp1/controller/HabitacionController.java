package pe.edu.upeu.project_final_gp1.controller;

import java.net.URL;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.project_final_gp1.HelloApplication;
import pe.edu.upeu.project_final_gp1.implementacion.HabitacionDaoImpl;
import pe.edu.upeu.project_final_gp1.modelo.Habitacion;
import pe.edu.upeu.project_final_gp1.utils.Alerts;

public class HabitacionController {

    @FXML private TableView<Habitacion> tableHabitacion;
    @FXML private TableColumn<Habitacion, Number> ColNum;
    @FXML private TableColumn<Habitacion, Integer> ColId;
    @FXML private TableColumn<Habitacion, String> ColNumero;
    @FXML private TableColumn<Habitacion, String> ColNumeroPiso;
    @FXML private TableColumn<Habitacion, String> ColNumeroHab;
    @FXML private TableColumn<Habitacion, String> ColTipo;
    @FXML private TableColumn<Habitacion, Double> Colprecio;
    @FXML private TableColumn<Habitacion, String> ColEstado;

    private final HabitacionDaoImpl habitacionDao = new HabitacionDaoImpl();
    private ObservableList<Habitacion> listaHabitacions;

    @FXML
    public void initialize() {
        ColNum.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        ColId.setCellValueFactory(data -> data.getValue().idhabitacionProperty().asObject());
        ColNumero.setCellValueFactory(data -> data.getValue().numeroProperty());
        ColNumeroPiso.setCellValueFactory(data -> data.getValue().numeroPisoProperty());
        ColNumeroHab.setCellValueFactory(data -> data.getValue().numeroHabitacionProperty());
        ColTipo.setCellValueFactory(data -> data.getValue().tipoProperty());
        Colprecio.setCellValueFactory(data -> data.getValue().precioProperty().asObject());
        ColEstado.setCellValueFactory(data -> data.getValue().estadoProperty());

        cargarDatos();
    }

    private void cargarDatos() {
        listaHabitacions = FXCollections.observableArrayList(habitacionDao.listAll());
        tableHabitacion.setItems(listaHabitacions);
    }

    @FXML
    private void handleNew() {
        showForm(null);
    }

    @FXML
    private void handleEdit() {
        Habitacion seleccionada = tableHabitacion.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una habitación para editar.");
            return;
        }
        showForm(seleccionada);
    }

    @FXML
    private void handleDelete() {
        Habitacion seleccionada = tableHabitacion.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una habitación para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Deseas eliminar esta habitación?");
        confirmacion.setContentText("Número: " + seleccionada.getNumero());
        Optional<ButtonType> respuesta = confirmacion.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            boolean resultado = habitacionDao.delete(seleccionada);
            if (resultado) {
                Alerts.viewAlerts("Éxito", "Habitación eliminada correctamente.", "CONFIRMATION").showAndWait();
                cargarDatos();
            } else {
                mostrarError("No se pudo eliminar la habitación. Verifica si tiene registros relacionados y revisa la consola.");
            }
        }
    }

    private void showForm(Habitacion habitacionExistente) {
        String rutaFXML = "/pe/edu/upeu/project_final_gp1/views/habitacion-form.fxml";
        try {
            URL ruta = HelloApplication.class.getResource(rutaFXML);
            if (ruta == null) {
                mostrarError("No se encontró el formulario: " + rutaFXML);
                return;
            }

            FXMLLoader loader = new FXMLLoader(ruta);
            Parent root = loader.load();
            HbitacionFromController controller = loader.getController();
            controller.setHabitacion(habitacionExistente);

            Stage stage = new Stage();
            stage.setTitle(habitacionExistente == null ? "Nueva habitación" : "Editar habitación");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (controller.isSaved()) {
                Habitacion habitacionProcesada = controller.getHabitacion();
                if (habitacionProcesada == null) {
                    mostrarError("No se obtuvieron los datos de la habitación.");
                    return;
                }

                boolean resultado = habitacionExistente == null
                        ? habitacionDao.insert(habitacionProcesada)
                        : habitacionDao.update(habitacionProcesada);

                if (resultado) {
                    Alerts.viewAlerts(
                            "Éxito",
                            habitacionExistente == null
                                    ? "Habitación registrada correctamente."
                                    : "Habitación actualizada correctamente.",
                            "CONFIRMATION"
                    ).showAndWait();
                    cargarDatos();
                } else {
                    mostrarError(habitacionExistente == null
                            ? "Error al registrar la habitación. Revisa la consola y la base de datos."
                            : "Error al actualizar la habitación. Revisa la consola y la base de datos.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir o procesar el formulario: " + e.getMessage());
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Gestión de habitaciones");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Gestión de habitaciones");
        alert.setHeaderText("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
