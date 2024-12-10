package controladores;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import modelos.Cliente;
import modelos.Compras;
import modelos.Juego;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 *
 * @author Juanan
 */
public class controladorAñadirCompra implements Initializable {

    private Controlador controladorst;
    Connection conexion;
    PreparedStatement ps;
    List<ValidationSupport> validadores;

    ObservableList<Compras> compraBD;
    ObservableList<Cliente> clienteBD;
    ObservableList<Juego> juegosBD;
    Integer idJuego;

    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Label lblFecha;
    @FXML
    private DatePicker fechaCompra;

    @FXML
    private Label lblDNI;
    @FXML
    private TextField tfDNI;
    @FXML
    private ListView<String> lvDni;

    @FXML
    private Label lblNombre;
    @FXML
    private TextField TFnJuego;
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
                    idJuego = selectedNombreJuego.getId_juego();
                    TFnJuego.setText(String.valueOf(idJuego));
                }
            }
        });
    }

    @FXML
    void aceptar(ActionEvent event) throws Exception {
        System.out.println("Comprobación de DATOS");
        System.out.println("=====================");
        for (ValidationSupport validationSupport : validadores) {
            ValidationResult resultados = validationSupport.getValidationResult();
            if (resultados != null) {
                System.out.println("Errores: " + resultados.getErrors());
                System.out.println("Infos: " + resultados.getInfos());
                System.out.println("Mensajes: " + resultados.getMessages());
                System.out.println("Warnings: " + resultados.getWarnings());
            } else {
                System.out.println("No hay resultados de validación para: " + validationSupport.getRegisteredControls());
            }
        }
        boolean todoOK = true;
        for (ValidationSupport validationSupport : validadores) {
            todoOK = (todoOK && validationSupport.getValidationResult().getErrors().isEmpty());
        }

        if (!todoOK) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("El formulario sigue teniendo ERRORES!!!");
            a.showAndWait();
            return;
        }
        if (controladorst.editrarBool != true) {
            String sql = "INSERT INTO Compras VALUES (?, ?, ?)";
            conectar(sql);
            ps.setDate(1, java.sql.Date.valueOf(fechaCompra.getValue()));
            ps.setString(2, tfDNI.getText());
            ps.setString(3, TFnJuego.getText());
            try {
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Adevertencia");
                alert.setHeaderText("La fecha que ha seleccionado ya existe.");
                alert.setContentText("Elija otra fecha.");
                alert.showAndWait();
                return;
            }

        } else {
            String sql = "UPDATE Compras SET dni=?, id_Juego=? WHERE Fecha_Compra=?";
            conectar(sql);
            ps.setString(1, tfDNI.getText());
            ps.setString(2, TFnJuego.getText());
            ps.setDate(3, java.sql.Date.valueOf(fechaCompra.getValue()));
            try {
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Adevertencia");
                alert.setContentText("La fecha que ha seleccionado ya existe.");
                alert.setContentText("Elija otra fecha.");
                alert.showAndWait();
                return;
            }
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
        ValidationSupport vsFecha = new ValidationSupport();

        //Validacion y decorador DatePicker
        vsFecha.registerValidator(fechaCompra, false, (Control c, Object value) -> {
            LocalDate fecha = fechaCompra.getValue();
            if (fecha == null) {
                return ValidationResult.fromError(c, "Debe seleccionar una fecha.");
            }
            return ValidationResult.fromInfo(c, "Fecha válida.");
        });
        GraphicValidationDecoration dFecha = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblFecha.setGraphic(iconoPersonalizadoEtiqueta());
                    lblFecha.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblFecha.setGraphic(null);
                }
            }
        };

        //Validacion y decorador Dni
        ValidationSupport vsDNI = new ValidationSupport();
        vsDNI.registerValidator(tfDNI, false, (Control c, Object value) -> {
            String dniInput = tfDNI.getText();
            if (dniInput == null || dniInput.isEmpty()) {
                return ValidationResult.fromError(c, "Debe ingresar un DNI.");
            }
            if (!lvDni.getItems().contains(dniInput)) {
                return ValidationResult.fromError(c, "El DNI ingresado no está en la lista.");
            }
            return ValidationResult.fromInfo(c, "DNI válido.");
        });
        GraphicValidationDecoration dDNI = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblDNI.setGraphic(iconoPersonalizadoEtiqueta());
                    lblDNI.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblDNI.setGraphic(null);
                }
            }
        };

        //Validacion y decorador ID
        ValidationSupport vsId = new ValidationSupport();
        vsId.registerValidator(TFnJuego, false, (Control c, Object value) -> {
            String idjuego = TFnJuego.getText();
            if (idjuego == null || idjuego.isEmpty()) {
                return ValidationResult.fromError(c, "Debe ingresar un ID.");
            }
            try {
                int id = Integer.parseInt(idjuego);
                boolean idExiste = idJuego == id;
                if (!idExiste) {
                    return ValidationResult.fromError(c, "El ID ingresado no está en la lista.");
                }
                return ValidationResult.fromInfo(c, "ID válido.");
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "El ID debe ser un número.");
            }
        });
        GraphicValidationDecoration dId = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblNombre.setGraphic(iconoPersonalizadoEtiqueta());
                    lblNombre.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblNombre.setGraphic(null);
                }
            }
        };

        validadores = new ArrayList<>();
        validadores.addAll(Arrays.asList(vsFecha, vsDNI, vsId));

        Platform.runLater(() -> {
            System.out.println(controladorst.editando());
            compraBD = controladorst.obtenerComprasBD();

            for (ValidationSupport validationSupport : validadores) {
                validationSupport.initInitialDecoration();
            }
            vsFecha.setValidationDecorator(dFecha);
            vsDNI.setValidationDecorator(dDNI);
            vsId.setValidationDecorator(dId);

            if (controladorst.editando() == true) {
                limpiarCampos();
                rellenarCamposEditar();
                rellenarLv();
            } else {
                limpiarCampos();
                rellenarLv();
            }
        }
        );
    }

    private Label iconoPersonalizadoEtiqueta() {
        Label errorLabel = new Label("X");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        return errorLabel;
    }
}
