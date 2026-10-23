package pe.edu.upeu.project_final_gp1.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pe.edu.upeu.project_final_gp1.modelo.Cliente;
import pe.edu.upeu.project_final_gp1.utils.Alerts;

public class ClienteFormController {
    private static Alerts alerts = new Alerts();
    @FXML
    private Label lblTitulo;

    @FXML
    private TextField txtNombres, txtApellidos, txtDni, txtEmail;

    @FXML
    private ComboBox<String> cbGenero;

    private Cliente cliente;
    private boolean saved = false;

    @FXML
    public void initialize(){

    }
    public void setCliente(Cliente c){
        this.cliente = c;
        if (c!=null){
            lblTitulo.setText("Editar cliente");
            txtNombres.setText(c.getNombres());
            txtApellidos.setText(c.getApellidos());
            txtDni.setText(c.getDni());
            txtEmail.setText(c.getEmail());
            cbGenero.setValue(c.getGenero());
        }
    }
    public boolean isSaved(){
        return saved;
    }

    public Cliente getCliente() {
        return cliente;
    }
    private void  cerrar(){
        Stage stage = (Stage) txtNombres.getScene().getWindow();
        stage.close();
    }
    private boolean validate(){
        if (txtNombres.getText().isEmpty() && txtDni.getText().isEmpty()) {
            Alert alert = Alerts.viewAlerts("ERROR","NOMBRE y DNI obligatorio","WARNING");
            alert.show();
            return false;
        }
        return true;
    }
    @FXML
    private void handleCancel(){
        cerrar();
    }
    @FXML
    private void handleSaved(){
        if (validate()) {
            if (cliente == null) cliente = new Cliente();
            cliente.setNombres(txtNombres.getText());
            cliente.setApellidos(txtApellidos.getText());
            cliente.setDni(txtDni.getText());
            cliente.setEmail(txtEmail.getText());
            cliente.setGenero(cbGenero.getValue());
            saved = true;
            cerrar();
        }
    }
}
