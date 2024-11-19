package controladores;

import modelos.Juego;
import modelos.Compras;
import modelos.Cliente;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
/**
 *
 * @author Juanan
 */
public class Controlador implements Initializable{
    Stage stageAñadir;
    @FXML
    private Button btnAñadir;

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnCompras;

    @FXML
    private Button btnJuegos;
    
    @FXML
    private StackPane contenedorTablas;

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> nombre;
    @FXML
    private TableColumn<Cliente, String> dni;
    @FXML
    private TableColumn<Cliente, String> email;
    @FXML
    private TableColumn<Cliente, Integer> telefono;
    @FXML
    private TableColumn<Cliente, Boolean> socio;
    
    @FXML
    private TableView<Compras> tablaCompras;
    @FXML
    private TableColumn<Cliente, Date> fecha;
    @FXML
    private TableColumn< Cliente, String> cliente;
    @FXML
    private TableColumn<Juego, Integer> idJuego;
    
    @FXML
    private TableView<Juego> tablaJuegos;
    @FXML
    private TableColumn<Juego, Integer> id;
    @FXML
    private TableColumn<Juego, ImageView> imagenJuego;
    @FXML
    private TableColumn<Juego, String> nombrejuego;
    @FXML
    private TableColumn<Juego, String> descripcion;
    @FXML
    private TableColumn<Juego, String> plataforma;
    @FXML
    private TableColumn<Juego, Double> precio;
    @FXML
    private TableColumn<Juego, Integer> stock;
    
    Integer tabla=1;
    @FXML
    void abrirVentanaClientes(ActionEvent event) {
        tabla=2;
        ocultarTodasLasTablas();
        tablaClientes.setVisible(true);
    }

    @FXML
    void abrirVentanaCompras(ActionEvent event) {
        tabla=1;
        ocultarTodasLasTablas();
        tablaCompras.setVisible(true);
    }

    @FXML
    void abrirVentanaJuegos(ActionEvent event) {
        tabla=3;
        ocultarTodasLasTablas();
        tablaJuegos.setVisible(true);
    }
    
    private void cerrarVentana(){
        stageAñadir.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Advertencia");
            alert.setHeaderText("¿Estás seguro de que deseas cerrar la ventana?");
            alert.setContentText("Los cambios realizados no se guardarán.");
            Optional<ButtonType> respuesta = alert.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                stageAñadir.close(); 
            }
        });
    }
    
    @FXML
    void añadirElemento(ActionEvent e) throws Exception{
        if(tabla==1){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ventanas/añadirCompras.fxml"));
            Parent root = loader.load();
            Scene scAñadir = new Scene(root);
            stageAñadir = new Stage();
            stageAñadir.initModality(Modality.APPLICATION_MODAL); 
            stageAñadir.setResizable(false);
            stageAñadir.setScene(scAñadir);
            stageAñadir.setTitle("Añadir");
            stageAñadir.show();
            cerrarVentana();
        }else if(tabla==2) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ventanas/añadirCliente.fxml"));
            Parent root = loader.load();
            Scene scAñadircompra = new Scene(root);
            stageAñadir = new Stage();
            stageAñadir.initModality(Modality.APPLICATION_MODAL);
            stageAñadir.setResizable(false);
            stageAñadir.setScene(scAñadircompra);
            stageAñadir.setTitle("Añadir");
            stageAñadir.show();
            cerrarVentana();
        }else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ventanas/añadirJuego.fxml"));
            Parent root = loader.load();
            Scene scAñadir = new Scene(root);
            stageAñadir = new Stage();
            stageAñadir.initModality(Modality.APPLICATION_MODAL);
            stageAñadir.setResizable(false);
            stageAñadir.setScene(scAñadir);
            stageAñadir.setTitle("Añadir");
            stageAñadir.show();
            cerrarVentana();
        }
    }
    
    private void ocultarTodasLasTablas() {
        for (Node node : contenedorTablas.getChildren()) {
            node.setVisible(false);
        }
    }
    
    private ObservableList<Cliente> obtenerClientesBD() {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList();
        try (Connection conectar = ConexionMySQL.conectarBaseDeDatos()) {
            String sql = """
                    SELECT dni, nombre, telefono, email, socio
                    FROM Clientes
                    """;
            PreparedStatement consulta = conectar.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery();
            while (resultado.next()) {
                 Cliente cliente = new Cliente(
                    resultado.getString("dni"),
                    resultado.getString("nombre"),
                    resultado.getString("email"),
                    resultado.getInt("telefono"),
                    resultado.getBoolean("socio")
                );
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    private ObservableList<Compras> obtenerComprasBD() {
        ObservableList<Compras> compras = FXCollections.observableArrayList();
        try (Connection conectar = ConexionMySQL.conectarBaseDeDatos()) {
            String sql = """
                    SELECT Fecha_Compra, dni, id_Juego
                    FROM Compras
                    """;
            PreparedStatement consulta = conectar.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery();
            while (resultado.next()) {
                 Compras compra = new Compras(
                    resultado.getDate("Fecha_Compra"),
                    resultado.getString("dni"),
                    resultado.getInt("id_Juego")
                );
                compras.add(compra);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compras;
    }
    private ObservableList<Juego> obtenerJuegosBD() {
        ObservableList<Juego> juegos = FXCollections.observableArrayList();
        try (Connection conectar = ConexionMySQL.conectarBaseDeDatos()) {
            String sql = """
                    SELECT id_Juego, Nombre, Descripcion, Plataforma, Imagen, Stock, Precio
                    FROM Juegos
                    """;
            PreparedStatement consulta = conectar.prepareStatement(sql);
            ResultSet resultado = consulta.executeQuery();
            while (resultado.next()) {
                 Juego juego = new Juego(
                    resultado.getInt("id_Juego"),
                    resultado.getString("Imagen"),
                    resultado.getString("Nombre"),
                    resultado.getString("Plataforma"),
                    resultado.getString("Descripcion"),
                    resultado.getInt("Stock"),
                    resultado.getDouble("Precio")
                );
                juegos.add(juego);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return juegos;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        socio.setCellValueFactory(new PropertyValueFactory<>("socio"));
        tablaClientes.setItems(obtenerClientesBD());
        
        fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cliente.setCellValueFactory(new PropertyValueFactory<>("dni"));
        idJuego.setCellValueFactory(new PropertyValueFactory<>("idjuego"));
        tablaCompras.setItems(obtenerComprasBD());
       
        id.setCellValueFactory(new PropertyValueFactory<>("id_juego"));
        imagenJuego.setCellValueFactory(cellData -> { 
            return cargarImagen(cellData.getValue().getImagen());
        });
        
        nombrejuego.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
        plataforma.setCellValueFactory(new PropertyValueFactory<>("Plataforma"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("Descripcion"));
        precio.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        stock.setCellValueFactory(new PropertyValueFactory<>("Stock"));
        tablaJuegos.setItems(obtenerJuegosBD());
        
        
        
    }
    private SimpleObjectProperty<ImageView> cargarImagen(String url) {
        try {
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("URL no válida");
            }

            Image image = new Image(url);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            return new SimpleObjectProperty<>(imageView);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            ImageView errorImageView = new ImageView(new Image("./prueba.jpg"));
            errorImageView.setFitHeight(50);
            errorImageView.setFitWidth(50);
            return new SimpleObjectProperty<>(errorImageView);
        }
    }
}
