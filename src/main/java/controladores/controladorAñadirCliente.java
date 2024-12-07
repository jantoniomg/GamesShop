/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import modelos.Cliente;
import modelos.Juego;

/**
 *
 * @author Juanan
 */
public class controladorAñadirCliente implements Initializable {

    private Controlador controladorst;
    Connection conexion;
    PreparedStatement ps;

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

    private void conectar(String sql) throws Exception {
        conexion = controladorst.getConnection();
        ps = conexion.prepareStatement(sql);
    }

    public void rellenarCamposEditar() {
        Cliente editarCliente = controladorst.dameCliente();
        ftDni.setText(editarCliente.getDni());
        ftNombre.setText(editarCliente.getNombre());
        ftEmail.setText(editarCliente.getEmail());
        ftTelefono.setText(editarCliente.getTelefono().toString());
        chbSocio.setSelected(editarCliente.getSocio());
    }

    @FXML
    void aceptar(ActionEvent event) throws Exception {
        if (controladorst.editando() != true) {
            String sql = "INSERT INTO Clientes VALUES (?, ?, ?, ?, ?)";
            conectar(sql);
            ps.setString(1, ftDni.getText());
            ps.setString(2, ftNombre.getText());
            ps.setString(3, ftTelefono.getText());
            ps.setString(4, ftEmail.getText());
            int socio = chbSocio.isSelected() ? 1 : 0;
            ps.setInt(5, socio);
            ps.executeUpdate();
        } else {
            String sql = "UPDATE Clientes SET nombre=?, telefono=?, email=?, socio=? WHERE dni=?";
            conectar(sql);
            ps.setString(1, ftNombre.getText());
            ps.setString(2, ftTelefono.getText());
            ps.setString(3, ftEmail.getText());
            int socio = chbSocio.isSelected() ? 1 : 0;
            ps.setInt(4, socio);
            ps.setString(5, ftDni.getText());
            ps.executeUpdate();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adevertencia");
        alert.setHeaderText("¿Estás seguro de que deseas ACEPTAR la operación?");
        alert.setContentText("Los cambios realizados se guardarán.");

        Optional<ButtonType> respuesta = alert.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            controladorst.introducirClientes();
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

    public void limpiarCampos() {
        ftDni.clear();
        ftNombre.clear();
        ftEmail.clear();
        ftTelefono.clear();
        chbSocio.disableProperty();
    }

    public void setControladorEnlace(Controlador control) {
        this.controladorst = control;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            System.out.println(controladorst.editando());
            if (controladorst.editando() == true) {
                limpiarCampos();
                rellenarCamposEditar();
            } else {
                limpiarCampos();
            }
        });
    }
}
