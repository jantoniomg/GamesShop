/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/**
 *
 * @author Juanan
 */
public class controladorAñadirCliente implements Initializable{
    
    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private CheckBox chbSocio;

    @FXML
    private TextField ftDni;

    @FXML
    private TextField ftEmail;

    @FXML
    private TextField ftNombre;

    @FXML
    private TextField ftTelefono;
    
    @FXML
    void aceptar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adevertencia");
        alert.setHeaderText("¿Estás seguro de que deseas ACEPTAR la operación?");
        alert.setContentText("Los cambios realizados se guardarán.");

        Optional<ButtonType> respuesta = alert.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void cancelar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("¿Estás seguro de que deseas CANCELAR la operación?");
        alert.setContentText("Los cambios realizados no se guardarán.");

        Optional<ButtonType> respuesta = alert.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

}

