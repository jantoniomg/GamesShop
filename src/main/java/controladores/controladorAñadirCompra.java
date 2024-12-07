package controladores;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.ListCell;
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
    PreparedStatement ps;

    ObservableList<Cliente> clienteBD;
    ObservableList<Juego> juegosBD;

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
    private ListView<Juego> lvNjuego;

    private void conectar(String sql) throws Exception {
        conexion = controladorst.getConnection();
        ps = conexion.prepareStatement(sql);
    }

    public void rellenarCamposEditar() {
        Compras editarCompras = controladorst.dameCompra();
        java.util.Date utilDate = editarCompras.getFecha();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        fechaCompra.setValue(sqlDate.toLocalDate());
        tfDNI.setText(editarCompras.getDni());
        TFnJuego.setText(editarCompras.getIdjuego().toString());
    }

    private void rellenarLv() {
        clienteBD = controladorst.obtenerClientesBD();
        juegosBD = controladorst.obtenerJuegosBD();

        ObservableList<String> dni = FXCollections.observableArrayList();
        ObservableList<Juego> juego = FXCollections.observableArrayList();

        for (Cliente i : clienteBD) {
            dni.add(i.getDni().toString());
        }

        for (Juego j : juegosBD) {
            juego.add(j);
        }

        FilteredList<String> filtroDni = new FilteredList<>(dni, p -> true);
        tfDNI.setPromptText("Introduce texto para filtrar..");
        tfDNI.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroDni.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return item.toLowerCase().contains(newValue.toLowerCase());
            });
        });
        FilteredList<Juego> filtrojuego = new FilteredList<>(juego, p -> true);
        TFnJuego.setPromptText("Introduce texto para filtrar..");
        TFnJuego.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrojuego.setPredicate(item -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                return item.getNombre().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        lvDni.setItems(filtroDni);
        lvNjuego.setItems(filtrojuego);

        lvNjuego.setCellFactory(lv -> new ListCell<Juego>() {
            @Override
            protected void updateItem(Juego juego, boolean empty) {
                super.updateItem(juego, empty);
                if (empty || juego == null) {
                    setText(null);
                } else {
                    setText(juego.getNombre());
                }
            }
        });

        lvDni.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                String selectedDni = lvDni.getSelectionModel().getSelectedItem();
                if (selectedDni != null) {
                    tfDNI.setText(selectedDni);
                }
            }
        });
        lvNjuego.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                Juego selectedNombreJuego = lvNjuego.getSelectionModel().getSelectedItem();
                if (selectedNombreJuego != null) {
                    Integer idJuego = selectedNombreJuego.getId_juego();
                    TFnJuego.setText(String.valueOf(idJuego));
                }
            }
        });
    }

    @FXML
    void aceptar(ActionEvent event) throws Exception {
        if (controladorst.editrarBool != true) {
            String sql = "INSERT INTO Compras VALUES (?, ?, ?)";
            conectar(sql);
            ps.setDate(1, java.sql.Date.valueOf(fechaCompra.getValue()));
            ps.setString(2, tfDNI.getText());
            ps.setString(3, TFnJuego.getText());
            ps.executeUpdate();
        } else {
            String sql = "UPDATE Compras SET dni=?, id_Juego=? WHERE Fecha_Compra=?";
            conectar(sql);
            ps.setString(1, tfDNI.getText());
            ps.setString(2, TFnJuego.getText());
            ps.setDate(3, java.sql.Date.valueOf(fechaCompra.getValue()));
            ps.executeUpdate();
        }
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
        System.out.println("añadiendo controlador" + control);
        this.controladorst = control;
    }

    public void limpiarCampos() {
        fechaCompra.setValue(null);
        TFnJuego.clear();
        tfDNI.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            if (controladorst.editando() == true) {
                limpiarCampos();
                rellenarCamposEditar();
            } else {
                limpiarCampos();
            }
            rellenarLv();
        });

    }

}
