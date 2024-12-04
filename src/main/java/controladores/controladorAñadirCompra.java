package controladores;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import modelos.Cliente;
import modelos.Compras;
import modelos.Juego;

/**
 *
 * @author Juanan
 */
public class controladorAñadirCompra implements Initializable {

    private Controlador controladorst;
    Connection conexion;

    @FXML
    private TextField TFnJuego;

    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private DatePicker fechaCompra;

    @FXML
    private TextField tfDNI;

    @FXML
    private ListView<String> lvDni;

    @FXML
    private ListView<String> lvNjuego;
    
    private void rellenarLv() {
        System.out.println("Relleno lv");
        System.out.println(controladorst);
        ObservableList<Cliente> clienteBD = controladorst.obtenerClientesBD();
        System.out.println(clienteBD);
        /*ObservableList<Juego> juegosBD = controladorst.obtenerJuegosBD();

        ObservableList<String> dni = FXCollections.observableArrayList();
        //ObservableList<Integer> idJuego = FXCollections.observableArrayList();
        //ObservableList<String> nombrejuego = FXCollections.observableArrayList();

        for (Cliente i : clienteBD) {
            dni.add(i.getDni().toString());
            System.out.println(i.getDni().toString());
        }
        for (Juego i : juegosBD) {
            idJuego.add(Integer.parseInt(i.getId_juego().toString()));
            nombrejuego.add(i.getNombre().toString());
            System.out.println(Integer.parseInt(i.getId_juego().toString()) + "\n" + i.getNombre().toString());
        }
        FilteredList<String> filtroDni = new FilteredList<>(dni, p -> true);
        tfDNI.setPromptText("Introduce texto para filtrar..");

        FilteredList<String> filtrojuego = new FilteredList<>(nombrejuego, p -> true);
        TFnJuego.setPromptText("Introduce texto para filtrar..");

        lvDni = new ListView<>(filtroDni);
        lvNjuego = new ListView<>(filtrojuego);*/

    }

    @FXML
    void aceptar(ActionEvent event) throws Exception {

        /*
        lvDni.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                String selectedDni = lvDni.getSelectionModel().getSelectedItem();
                if (selectedDni != null) {
                    //Ahora, en vez de la serie añadimos el año asociado
                    tfDNI.setText(clienteBD.get(dni.indexOf(selectedDni)).toString());
                }
            }
        });
        
        String query = "INSERT INTO Compra VALUES (?, ?, ?)";
        try {
            Connection conexion = controladorst.getConnection();
            PreparedStatement preparedStatement = conexion.prepareStatement(query);
            
            preparedStatement.setString(2, tfDNI.getText());
            preparedStatement.setString(3, TFnJuego.getText());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Excepción: "+e.getMessage());
        }
        */
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adevertencia");
        alert.setHeaderText("¿Estás seguro de que deseas ACEPTAR la operación?");
        alert.setContentText("Los cambios realizados se guardarán.");

        Optional<ButtonType> respuesta = alert.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            controladorst.introducirCompras();
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

    public void setControladorEnlace(Controlador control) {
        System.out.println("añadiendo controlador"+control);
        this.controladorst = control;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rellenarLv();
    }

}
