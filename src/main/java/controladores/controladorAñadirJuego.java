package controladores;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
public class controladorAñadirJuego implements Initializable {

    private Controlador controladorst;
    Connection conexion;
    PreparedStatement ps;
    List<ValidationSupport> validadores;

    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Label lblImagen;
    @FXML
    private ImageView ivImagen;

    @FXML
    private Button btnAñadirImagen;

    @FXML
    private Label lblDescripcion;
    @FXML
    private TextArea taDescripcion;

    @FXML
    private TextField tfId;

    @FXML
    private Label lblNombre;
    @FXML
    private TextField tfNombre;

    @FXML
    private ComboBox<String> cmbPlataforma;

    @FXML
    private Label lblPrecio;
    @FXML
    private TextField tfPrecio;

    @FXML
    private Label lblStock;
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
        ps = conexion.prepareStatement(sql);
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
            System.out.println("Se ha editado");
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

        tfNombre.setTooltip(new Tooltip("Nombre del juego. El nombre debe tener mas 5 caracteres"));
        tfPrecio.setTooltip(new Tooltip("Debe ser un numero positivo"));
        tfStock.setTooltip(new Tooltip("Debe ser un numero positivo"));
        taDescripcion.setTooltip(new Tooltip("El campo debe tener mas de 5 caracteres"));
        btnAñadirImagen.setTooltip(new Tooltip("Debes introducir una imagen"));

        //Validacion y decorador Nombre
        ValidationSupport vsNombre = new ValidationSupport();
        vsNombre.registerValidator(tfNombre, (Control c, Object value) -> {
            String nombre = (String) value;
            String nombreRegex = "^[A-Za-z0-9-_ ]{2,100}$";
            if (nombre == null) {
                return ValidationResult.fromWarning(c, "Val 3: El nombre no debe estar vacío");
            } else if (!nombre.matches(nombreRegex)) {
                return ValidationResult.fromError(c, "El nombre debe tener mas de 2 caracteres.");
            } else {
                return ValidationResult.fromInfo(c, "Val 3: Nombre valido");
            }

        });
        GraphicValidationDecoration dNombre = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblNombre.setGraphic(iconoPersonalizadoEtiqueta());
                    lblNombre.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblNombre.setGraphic(null);
                }
            }
        };

        //Validacion y decorador Stock
        ValidationSupport vsStock = new ValidationSupport();
        vsStock.registerValidator(tfStock, (Control c, Object value) -> {
            String stock = (String) value;
            String stockRegex = "^[1-9][0-9]{0,3}$";
            if (stock == null) {
                return ValidationResult.fromWarning(c, "Val 3: El Stock no debe estar vacío");
            } else if (!stock.matches(stockRegex)) {
                return ValidationResult.fromError(c, "El stock debe ser un numero positivo mayor de 0");
            } else {
                return ValidationResult.fromInfo(c, "Val 3: Stock valido");
            }
        });
        GraphicValidationDecoration dStock = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblStock.setGraphic(iconoPersonalizadoEtiqueta());
                    lblStock.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblStock.setGraphic(null);
                }
            }
        };

        //Validacion y decorador Precio
        ValidationSupport vsPrecio = new ValidationSupport();
        vsPrecio.registerValidator(tfPrecio, (Control c, Object value) -> {
            String precioStr = (String) value;
            String precioRegex = "^(\\d{1,2})(\\.\\d{1,2})?$";
            if (precioStr == null) {
                return ValidationResult.fromWarning(c, "Val 3: El Stock no debe estar vacío");
            } else if (!precioStr.matches(precioRegex)) {
                return ValidationResult.fromError(c, "El precio debe ser un número entre 0 y 99.99.");
            }
            try {
                double precio = Double.parseDouble(precioStr);
                if (precio > 99.99) {
                    return ValidationResult.fromError(c, "El precio no puede ser mayor que 99.99.");
                }
            } catch (NumberFormatException e) {
                return ValidationResult.fromError(c, "El precio debe ser un número válido.");
            }

            return ValidationResult.fromInfo(c, "Val 3: Precio valido");
        });
        GraphicValidationDecoration dPrecio = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblPrecio.setGraphic(iconoPersonalizadoEtiqueta());
                    lblPrecio.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblPrecio.setGraphic(null);
                }
            }
        };

        //Validacion y decorador Precio
        ValidationSupport vsDescripcion = new ValidationSupport();
        vsDescripcion.registerValidator(taDescripcion, (Control c, String texto) -> {
            if (texto.length() == 0) {
                return ValidationResult.fromWarning(c, "Val 3: El nombre no debe estar vacío");
            } else if (texto.length() < 5) {
                return ValidationResult.fromError(c, "Val 3: El nombre debe tener mas 5 caracteres");
            } else {
                return ValidationResult.fromInfo(c, "Val 3: Precio valido");
            }
        });
        GraphicValidationDecoration dDescripcion = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblDescripcion.setGraphic(iconoPersonalizadoEtiqueta());
                    lblDescripcion.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblDescripcion.setGraphic(null);
                }
            }
        };
        /*
        //Validacion y decorador Imagen
        ValidationSupport vsImagen = new ValidationSupport();
        vsImagen.registerValidator(lblImagen, false, (Control c, Object value) -> {
            Image image = ivImagen.getImage();
            if (image == null) {
                return ValidationResult.fromError(c, "Debe cargar una imagen.");
            }
            return ValidationResult.fromInfo(c, "Val 3: Imagen valida");
        });
        GraphicValidationDecoration dImagen = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                if (message.getSeverity() == Severity.ERROR) {
                    lblImagen.setGraphic(iconoPersonalizadoEtiqueta());
                    lblImagen.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblImagen.setGraphic(null);
                }
            }
        };*/

        validadores = new ArrayList<>();
        validadores.addAll(Arrays.asList(vsNombre, vsStock, vsPrecio, vsDescripcion /*, vsImagen*/));

        Platform.runLater(() -> {
            System.out.println(controladorst.editando());
            for (ValidationSupport validationSupport : validadores) {
                validationSupport.initInitialDecoration();
            }
            vsNombre.setValidationDecorator(dNombre);
            //vsImagen.setValidationDecorator(dImagen);
            vsStock.setValidationDecorator(dStock);
            vsPrecio.setValidationDecorator(dPrecio);
            vsDescripcion.setValidationDecorator(dDescripcion);

            if (controladorst.editando() == true) {
                limpiarCampos();
                rellenarCamposEditar();
            } else {
                limpiarCampos();
            }
        });
        
        for (ValidationSupport vs : validadores) {
            vs.validationResultProperty().addListener((observable, oldValue, newValue) -> {
                Set<Control> controles = vs.getRegisteredControls();
                System.out.println(controles.size());
                for (Control c : controles) {
                    System.out.println(c);
                    if (newValue.getErrors().isEmpty() && newValue.getWarnings().isEmpty()) {
                        c.getStyleClass().remove("error");
                        c.setEffect(creaDropShadow(Color.GREEN));
                    } else {
                        if (!c.getStyleClass().contains("error")) {
                            c.getStyleClass().add("error");
                            c.setEffect(creaDropShadow(Color.RED));
                        }
                    }
                }
            });
        }
    }

    private DropShadow creaDropShadow(Color c) {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(c);
        return dropShadow;
    }

    private Label iconoPersonalizadoEtiqueta() {
        Label errorLabel = new Label("X");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        return errorLabel;
    }
}
