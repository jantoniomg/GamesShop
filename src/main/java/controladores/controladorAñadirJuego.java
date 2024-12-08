package controladores;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelos.Juego;

/**
 *
 * @author Juanan
 */
public class controladorAñadirJuego implements Initializable {

    private Controlador controladorst;
    Connection conexion;
    PreparedStatement ps;
    
    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private ImageView ivImagen;

    @FXML
    private Button btnAñadirImagen;

    @FXML
    private TextArea taDescripcion;

    @FXML
    private TextField tfId;

    @FXML
    private TextField tfNombre;

    @FXML
    private ComboBox<String> cmbPlataforma;

    @FXML
    private TextField tfPrecio;

    @FXML
    private TextField tfStock;

    @FXML
    void añadirImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            Image image = new Image(archivoSeleccionado.toURI().toString());
            ivImagen.setImage(image);
        }
    }

    private void conectar(String sql) throws Exception {
        conexion = controladorst.getConnection();
        ps= conexion.prepareStatement(sql);
    }

    public void rellenarCamposEditar() {
        Juego editarJuego = controladorst.dameJuego();
        ivImagen.setImage(new Image(editarJuego.getImagen()));
        taDescripcion.setText(editarJuego.getDescripcion());
        tfId.setText(editarJuego.getId_juego().toString());
        tfNombre.setText(editarJuego.getNombre());
        cmbPlataforma.setValue(editarJuego.getPlataforma());
        tfPrecio.setText(editarJuego.getPrecio().toString());
        tfStock.setText(String.valueOf(editarJuego.getStock()));
    }

    @FXML
    void aceptar(ActionEvent event) throws Exception {
        if (controladorst.editando() != true) {
            String sql = "INSERT INTO Juegos (`Nombre`, `Descripcion`, `Plataforma`, `Imagen`, `Stock`, `Precio`) VALUES (?, ?, ?, ?, ?, ?)";
                conectar(sql);
                ps.setString(1, tfNombre.getText());
                ps.setString(2, taDescripcion.getText());
                ps.setString(3, cmbPlataforma.getValue());
                ps.setString(4, ivImagen.getImage().getUrl());
                ps.setInt(5, Integer.parseInt(tfStock.getText()));
                ps.setDouble(6, Double.parseDouble(tfPrecio.getText()));
                ps.executeUpdate();
        } else {
            String sql = "UPDATE Juegos SET Nombre=?, Descripcion=?, Plataforma=?, Imagen=?, Stock=?, Precio=?  WHERE id_Juego=?";
                conectar(sql);
                ps.setString(1, tfNombre.getText());
                ps.setString(2, taDescripcion.getText());
                ps.setString(3, cmbPlataforma.getValue());
                ps.setString(4, ivImagen.getImage().getUrl());
                ps.setInt(5, Integer.parseInt(tfStock.getText()));
                ps.setDouble(6, Double.parseDouble(tfPrecio.getText()));
                ps.setString(7, tfId.getText());
                ps.executeUpdate();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adevertencia");
        alert.setHeaderText("¿Estás seguro de que deseas ACEPTAR la operación?");
        alert.setContentText("Los cambios realizados se guardarán.");

        Optional<ButtonType> respuesta = alert.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            controladorst.introducirJuegos();
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
            controladorst.introducirJuegos();
            stage.close();
        }
    }

    public void limpiarCampos() {
        ivImagen.setImage(null);
        taDescripcion.setText("");
        tfId.clear();
        tfNombre.clear();
        cmbPlataforma.setValue("PC");
        tfPrecio.clear();
        tfStock.clear();
    }

    public void setControladorEnlace(Controlador control) {
        this.controladorst = control;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> plataformas = FXCollections.observableArrayList("Nintendo Switch", "PC", "PS4", "PS5", "XBOX");
        cmbPlataforma.getItems().addAll(plataformas);
        Platform.runLater(() -> {
            System.out.println(controladorst.editando());
            if (controladorst.editando() == true) {
                limpiarCampos();
                rellenarCamposEditar();
            }else{
                limpiarCampos();
            }
        });
        
    }
}
