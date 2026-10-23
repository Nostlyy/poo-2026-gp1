package pe.edu.upeu.project_final_gp1.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.project_final_gp1.HelloApplication;
import pe.edu.upeu.project_final_gp1.implementacion.ClienteDaoImpl;
import pe.edu.upeu.project_final_gp1.interfaces.IClienteDao;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;
import pe.edu.upeu.project_final_gp1.utils.Alerts;

import java.util.Optional;

public class ClienteController {
    private static Alerts alerts = new Alerts();
    @FXML
    private TableView<Cliente> tableCliente;
    @FXML
    private TableColumn<Cliente, Integer> ColId;
    @FXML
    private TableColumn<Cliente, Number> ColNumero;
    @FXML
    private TableColumn<Cliente, String> ColNombre;
    @FXML
    private TableColumn<Cliente, String> ColApellido;
    @FXML
    private TableColumn<Cliente, String> ColDni;
    @FXML
    private TableColumn<Cliente, String> ColEmail;
    @FXML
    private TableColumn<Cliente, String> ColGenero;

    private final IClienteDao clienteDao = new ClienteDaoImpl();
    private ObservableList<Cliente> listaCliente;

    @FXML
    public void initialize() {
        ColNumero.setCellFactory(column-> new TableCell<>(){
            @Override
            protected void updateItem(Number item,boolean empaty){
                super.updateItem(item,empaty);{
                    if (empaty){
                        setText(null);
                    }else{
                        setText(String.valueOf(getIndex()+1));
                    }
                }
            }
        });

        ColId.setCellValueFactory(cellData -> cellData.getValue().idclienteProperty().asObject());
        ColNombre.setCellValueFactory(cellData -> cellData.getValue().nombresProperty());
        ColApellido.setCellValueFactory(cellData -> cellData.getValue().apellidosProperty());
        ColDni.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        ColEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        ColGenero.setCellValueFactory(cellData -> cellData.getValue().generoProperty());
        //Carga de datos
        cargarDatos();

    }

    private void cargarDatos() {
        listaCliente = FXCollections.observableArrayList(clienteDao.listAll());
        tableCliente.setItems(listaCliente);

    }
    @FXML
    private void handleDelete(){
        Cliente selectID = tableCliente.getSelectionModel().getSelectedItem();
        if (selectID != null){
            Alert confirm = alerts.viewAlerts("Confirmar Eliminacion",
                    "Estas seguro de Eliminar?" +selectID.getNombres(), "CONFIRMATION");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get()==ButtonType.OK){
                if (clienteDao.delete(selectID)){
                    listaCliente.remove(selectID);
                }
            }else {
                Alert confirmWarning = alerts.viewAlerts("Seleccion requerida", null, "WARNING");
                confirmWarning.showAndWait();
            }
        }
    }
    @FXML
    private void handleNew(){
        // System.out.println("abriendo el formulario");
        showForm(null);

    }
    @FXML
    private void handleEdit(){
        Cliente selectClient = tableCliente.getSelectionModel().getSelectedItem();
        if (selectClient !=null){
            Cliente clientFull = clienteDao.searchById(selectClient.getIdcliente());
            showForm(clientFull);
        }else{
            Alert alert = Alerts.viewAlerts("Seleccion requerida","","CONFIRMATION");
            alert.show();
        }
    }
    private void showForm(Cliente c){
        try{
            FXMLLoader loader = new FXMLLoader(HelloApplication.class
                    .getResource("/pe/edu/upeu/project_final_gp1/views/cliente-form.fxml"));
            Parent root = loader.load();
            ClienteFormController controller = loader.getController();
            controller.setCliente(c); //pasamos el cliente null si es nuevo
            Stage stage = new Stage();
            stage.setTitle(c==null ? "Nuevo Cliente": "Editar Cliente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene( new Scene(root));
            stage.showAndWait();
            if (controller.isSaved()){
                Cliente clienteProcesado = controller.getCliente();
                if (c == null){
                    boolean result =  clienteDao.insert(clienteProcesado);
                    if (result){
                        Alert alert = Alerts.viewAlerts("Success","Registro satisfactorio!!!","CONFIRMATION");
                        alert.show();
                    }else{
                        Alert alert = Alerts.viewAlerts("Error","Error al insertar!!!","ERROR");
                        alert.show();
                    }
                }else{
                    boolean result =  clienteDao.update(clienteProcesado);
                    if (result){
                        Alert alert = Alerts.viewAlerts("Update",
                                "Registro actualizado!!!","WARNING");
                        alert.showAndWait();
                    }else{
                        Alert alert = Alerts.viewAlerts("Error",
                                "Error al actualizar!!!","ERROR");
                        alert.show();
                    }
                }
                cargarDatos();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}