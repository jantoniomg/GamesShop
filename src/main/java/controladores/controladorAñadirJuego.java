package controladores;

    import java.io.File;
    import java.net.URL;
    import java.util.Optional;
    import java.util.ResourceBundle;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.Initializable;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.ButtonType;
    import javafx.scene.control.TextArea;
    import javafx.scene.control.TextField;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.stage.FileChooser;
    import javafx.stage.Stage;
/**
 *
 * @author Juanan
 */
public class controladorAñadirJuego implements Initializable{
    
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
    private TextField tfPlataforma;

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

        // Verificar si se seleccionó un archivo
        if (archivoSeleccionado != null) {
            // Cargar la imagen seleccionada en el ImageView
            Image image = new Image(archivoSeleccionado.toURI().toString());
            ivImagen.setImage(image);
        }
    }
    
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

