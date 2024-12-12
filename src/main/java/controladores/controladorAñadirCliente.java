/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modelos.Cliente;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

/**
 *
 * @author Juanan
 */
public class controladorAñadirCliente implements Initializable {

    private Controlador controladorst;
    Connection conexion;
    PreparedStatement ps;
    List<ValidationSupport> validadores;
    Cliente editarCliente;
    Stage alertStage;

    @FXML
    private Button btnAceptar;

    @FXML
    private Button btnCancelar;

    @FXML
    private CheckBox chbSocio;

    @FXML
    private Label lblDni;
    @FXML
    private TextField ftDni;

    @FXML
    private Label lblEmail;
    @FXML
    private TextField ftEmail;

    @FXML
    private Label lblNombre;
    @FXML
    private TextField ftNombre;

    @FXML
    private Label lblTelefono;
    @FXML
    private TextField ftTelefono;

    private void conectar(String sql) throws Exception {
        conexion = controladorst.getConnection();
        ps = conexion.prepareStatement(sql);
    }

    public void rellenarCamposEditar() {
        editarCliente = controladorst.dameCliente();
        ftDni.setText(editarCliente.getDni());
        ftNombre.setText(editarCliente.getNombre());
        ftEmail.setText(editarCliente.getEmail());
        ftTelefono.setText(editarCliente.getTelefono().toString());
        chbSocio.setSelected(editarCliente.getSocio());
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
            Image icon = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
            alertStage = (Stage) a.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(icon);
            a.showAndWait();
            return;
        }
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
            String sql = "UPDATE Clientes SET nombre=?, telefono=?, email=?, socio=?, dni=? WHERE dni=?";
            conectar(sql);
            ps.setString(1, ftNombre.getText());
            ps.setString(2, ftTelefono.getText());
            ps.setString(3, ftEmail.getText());
            int socio = chbSocio.isSelected() ? 1 : 0;
            ps.setInt(4, socio);
            ps.setString(5, ftDni.getText());
            ps.setString(6, editarCliente.getDni());
            ps.executeUpdate();
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Adevertencia");
        alert.setHeaderText("¿Estás seguro de que deseas ACEPTAR la operación?");
        alert.setContentText("Los cambios realizados se guardarán.");
        Image icon = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
        alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(icon);
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
        Image icon = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
        alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(icon);
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
        ftDni.setTooltip(new Tooltip("DNI del cliente. El dni debe tener mas 8 numeros y una letra"));
        ftNombre.setTooltip(new Tooltip("Nombre de usuario. El nombre debe tener mas de 3 caracteres"));
        ftEmail.setTooltip(new Tooltip("Formato <texto>@<texto>.<texto>"));
        ftTelefono.setTooltip(new Tooltip("El telefono debe tener entre 9 y 11"));

        //Validacion y decorador DNI
        ValidationSupport vsDni = new ValidationSupport();
        vsDni.registerValidator(ftDni, (Control c, Object value) -> {
            String dni = (String) value;
            String dniRegex = "^[0-9]{8}[A-Za-z]$";
            if (dni == null) {
                return ValidationResult.fromWarning(c, "Val 3: El dni no debe estar vacío");
            } else if (!dni.matches(dniRegex)) {
                return ValidationResult.fromError(c, "El DNI debe tener 8 dígitos seguidos de una letra.");
            } else {
                return ValidationResult.fromInfo(c, "Val 3: DNI valido");
            }
        });
        GraphicValidationDecoration dDNI = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblDni.setGraphic(iconoPersonalizadoEtiqueta());
                    lblDni.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblDni.setGraphic(null);
                }
            }
        };

        //Validacion y decorador Nombre
        ValidationSupport vsNombre = new ValidationSupport();
        vsNombre.registerValidator(ftNombre, (Control c, Object value) -> {
            String nombre = (String) value;
            String nombreRegex = "^[A-Za-záéíóúÁÉÍÓÚÑñ ]{2,50}$";

            if (nombre == null) {
                return ValidationResult.fromWarning(c, "Val 3: El nombre no debe estar vacío");
            } else if (!nombre.matches(nombreRegex)) {
                return ValidationResult.fromError(c, "Val 3: El nombre debe tener mas 3 letras");
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
        //Validacion y decorador Email
        ValidationSupport vsEmail = new ValidationSupport();
        vsEmail.registerValidator(ftEmail, (Control c, Object value) -> {
            String email = (String) value;
            String emailRegex = "^(.+)@(.+)\\.(.+)$";
            if (email == null) {
                return ValidationResult.fromWarning(c, "Val 3: El email no debe estar vacío");
            } else if (!email.matches(emailRegex)) {
                return ValidationResult.fromError(c, "El email debe tener el siguiente formato Formato ( <texto>@<texto>.<texto> )");
            } else {
                return ValidationResult.fromInfo(c, "Val 3: Email valido");
            }
        });
        GraphicValidationDecoration dEmail = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblEmail.setGraphic(iconoPersonalizadoEtiqueta());
                    lblEmail.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblEmail.setGraphic(null);
                }
            }
        };
        //Validacion y decorador Telefono
        ValidationSupport vsTelefono = new ValidationSupport();
        vsTelefono.registerValidator(ftTelefono, (Control c, Object value) -> {
            String telefono = (String) value;
            String telefonoRegex = "^\\d{9,11}$";
            if (telefono == null) {
                return ValidationResult.fromWarning(c, "Val 3: El telefono no debe estar vacío");
            } else if (!telefono.matches(telefonoRegex)) {
                return ValidationResult.fromError(c, "El número de teléfono debe tener entre 9 y 11 dígitos.");
            } else {
                return ValidationResult.fromInfo(c, "Val 3: Email valido");
            }
        });
        GraphicValidationDecoration dTelefono = new GraphicValidationDecoration() {
            @Override
            public void applyValidationDecoration(ValidationMessage message) {
                super.applyValidationDecoration(message);
                System.out.println("Mensaje:" + message);
                if (message.getSeverity() == Severity.ERROR || message.getSeverity() == Severity.WARNING) {
                    lblTelefono.setGraphic(iconoPersonalizadoEtiqueta());
                    lblTelefono.setContentDisplay(ContentDisplay.LEFT);
                } else if (message.getSeverity() == Severity.INFO) {
                    lblTelefono.setGraphic(null);
                }
            }
        };

        validadores = new ArrayList<>();
        validadores.addAll(Arrays.asList(vsNombre, vsDni, vsEmail, vsTelefono));

        Platform.runLater(() -> {
            System.out.println(controladorst.editando());
            for (ValidationSupport validationSupport : validadores) {
                validationSupport.initInitialDecoration();
            }
            vsNombre.setValidationDecorator(dNombre);
            vsDni.setValidationDecorator(dDNI);
            vsEmail.setValidationDecorator(dEmail);
            vsTelefono.setValidationDecorator(dTelefono);

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
