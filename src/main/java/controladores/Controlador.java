package controladores;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.exit;
import modelos.Juego;
import modelos.Compras;
import modelos.Cliente;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Juanan
 */
public class Controlador implements Initializable {

    private controladorAñadirCliente conAñadirCliente;
    private controladorAñadirJuego conAñadirJuego;
    private controladorAñadirCompra conAñadirCompra;
    private ObservableList<Cliente> clientes;
    
    Stage stageAñadir;
    Connection conexion;
    Statement st;
    ResultSet rs;

    @FXML
    private ImageView imgAñadir;
    @FXML
    private Button btnAñadir;

    @FXML
    private ImageView imgClientes;
    @FXML
    private Button btnClientes;

    @FXML
    private ImageView imgCompra;
    @FXML
    private Button btnCompras;

    @FXML
    private ImageView imgJuegos;
    @FXML
    private Button btnJuegos;

    @FXML
    private Button actualizar;
    @FXML
    private Button eliminar;

    @FXML
    private Button btnSalir;

    @FXML
    private StackPane contenedorTablas;

    @FXML
    private Pane paneClientes;
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
    private Pane paneCompras;
    @FXML
    private TableView<Compras> tablaCompras;
    @FXML
    private TableColumn<Cliente, Date> fecha;
    @FXML
    private TableColumn< Cliente, String> cliente;
    @FXML
    private TableColumn<Juego, Integer> idJuego;

    @FXML
    private Pane paneJuegos;
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

    Integer tabla = 1;

    private void ocultar() {
        for (Node node : contenedorTablas.getChildren()) {
            node.setVisible(false);
        }
        paneCompras.setVisible(true);
        tablaCompras.setVisible(true);
    }

    @FXML
    void abrirVentanaCompras(ActionEvent event) {
        tabla = 1;
        ocultarTodasLasTablas();
        paneCompras.setVisible(true);
        tablaCompras.setVisible(true);
        eliminar.setVisible(true);
        actualizar.setVisible(true);
        ContextMenu cmCompras = new ContextMenu();
        MenuItem ver = new MenuItem("ver");
        MenuItem editar = new MenuItem("editar");
        MenuItem eliminar = new MenuItem("eliminar");
        cmCompras.getItems().addAll(ver, editar, eliminar);
        tablaCompras.setContextMenu(cmCompras);

        tablaCompras.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                eliminar.setOnAction(v -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText("¿Estás seguro que deseas eliminar la compra?");
                    alert.setContentText("La compra se eliminara para siempre");
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                        String sql = "DELETE FROM Compras WHERE Fecha_Compra=?";
                        try {
                            PreparedStatement ps = conexion.prepareStatement(sql);
                            ps.setDate(1, new java.sql.Date(nuevoValor.getFecha().getTime()));
                            ps.executeUpdate();
                            introducirCompras();
                            System.out.println("Se ha eliminado la compra del " + nuevoValor.getFecha());
                        } catch (SQLException exception) {
                            System.out.println("Excepción: " + exception.getMessage());
                        }
                    }
                });
            }
        });
    }
    void botonesInvisibles(){
        eliminar.setVisible(false);
        actualizar.setVisible(false);
    }
    @FXML
    void actualizarCompra(ActionEvent event) {
        
    }

    @FXML
    void eliminarCompra(ActionEvent event) {
        botonesInvisibles();
        
        Compras compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Advertencia");
            alert.setHeaderText("¿Estás seguro que deseas eliminar la compra?");
            alert.setContentText("La compra se eliminara para siempre");
            Optional<ButtonType> respuesta = alert.showAndWait();
            if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                String sql = "DELETE FROM Compras WHERE Fecha_Compra=?";
                try {
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setDate(1, new java.sql.Date(compra.getFecha().getTime()));
                    ps.executeUpdate();
                    introducirCompras();
                    System.out.println("Se ha eliminado la compra del " + compra.getFecha());
                } catch (SQLException exception) {
                    System.out.println("Excepción: " + exception.getMessage());
                }
            } else {
                System.out.println("Selecciona una Compra para eliminarla");
            }
        }
    }

    @FXML
    void abrirVentanaClientes(ActionEvent event) {
        botonesInvisibles();
        
        tabla = 2;
        ocultarTodasLasTablas();
        paneClientes.setVisible(true);
        tablaClientes.setVisible(true);
        
        ContextMenu cmClientes = new ContextMenu();
        MenuItem ver = new MenuItem("ver");
        MenuItem editar = new MenuItem("editar");
        MenuItem eliminar = new MenuItem("eliminar");
        cmClientes.getItems().addAll(ver, editar, eliminar);
        tablaClientes.setContextMenu(cmClientes);

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                eliminar.setOnAction(v -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText("¿Estás seguro de que deseas eliminar el cliente?");
                    alert.setContentText("El cliente se eliminara para siempre");
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                        String sql = "DELETE FROM Clientes WHERE dni=?";
                        try {
                            PreparedStatement ps = conexion.prepareStatement(sql);
                            ps.setString(1, nuevoValor.getDni());
                            ps.executeUpdate();
                            introducirClientes();
                            System.out.println("Se ha eliminado el Cliente con Dni " + nuevoValor.getDni());
                        } catch (SQLException exception) {
                            System.out.println("Excepción: " + exception.getMessage());
                        }
                    }
                });
            }
        });
    }

    @FXML
    void abrirVentanaJuegos(ActionEvent event) {
        tabla = 3;
        ocultarTodasLasTablas();
        paneJuegos.setVisible(true);
        tablaJuegos.setVisible(true);

        ContextMenu cmJuegos = new ContextMenu();
        MenuItem ver = new MenuItem("ver");
        MenuItem editar = new MenuItem("editar");
        MenuItem eliminar = new MenuItem("eliminar");
        cmJuegos.getItems().addAll(ver, editar, eliminar);
        tablaJuegos.setContextMenu(cmJuegos);
        tablaJuegos.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                eliminar.setOnAction(v -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText("¿Estás seguro de que deseas eliminar el juego?");
                    alert.setContentText("El juego se eliminara para siempre");
                    Optional<ButtonType> respuesta = alert.showAndWait();
                    if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
                        String sql = "DELETE FROM Juegos WHERE id_Juego=?";
                        try {
                            PreparedStatement ps = conexion.prepareStatement(sql);
                            ps.setInt(1, nuevoValor.getId_juego());
                            ps.executeUpdate();
                            introducirJuegos();
                            System.out.println("Se ha eliminado el juego con ID " + nuevoValor.getId_juego());
                        } catch (SQLException exception) {
                            System.out.println("Excepción: " + exception.getMessage());
                        }
                    }
                });
            }
        });
    }
    
    private void cerrarVentana() {
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

    void controladorAcontroladorCliente(Cliente cli) {
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                Cliente cl = new Cliente(nuevoValor.getDni(), nuevoValor.getNombre(), nuevoValor.getTelefono(), nuevoValor.getEmail(), nuevoValor.getSocio());
            }
        });
    }

    @FXML
    void añadirElemento(ActionEvent e) throws Exception {
        if (tabla == 1) {
            System.out.println("ENTRANDO EN COMPRAS");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ventanas/añadirCompras.fxml"));
            Parent root = loader.load();
            conAñadirCompra = loader.getController();
            System.out.println("hola"+this);
            conAñadirCompra.setControladorEnlace(this);
            Scene scAñadirCompra = new Scene(root);
            stageAñadir = new Stage();
            stageAñadir.initModality(Modality.APPLICATION_MODAL);
            stageAñadir.setResizable(false);
            stageAñadir.setScene(scAñadirCompra);
            stageAñadir.setTitle("Añadir Compra");
            stageAñadir.show();
            cerrarVentana();

        } else if (tabla == 2) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ventanas/añadirCliente.fxml"));
            Parent root = loader.load();
            conAñadirCliente = loader.getController();
            conAñadirCliente.setControladorEnlace(this);
            Scene scAñadirCliente = new Scene(root);
            stageAñadir = new Stage();
            stageAñadir.initModality(Modality.APPLICATION_MODAL);
            stageAñadir.setResizable(false);
            stageAñadir.setScene(scAñadirCliente);
            stageAñadir.setTitle("Añadir Cliente");
            stageAñadir.show();
            cerrarVentana();

        } else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../ventanas/añadirJuego.fxml"));
            Parent root = loader.load();
            conAñadirJuego = loader.getController();
            conAñadirJuego.setControladorEnlace(this);
            Scene scAñadirJuego = new Scene(root);
            stageAñadir = new Stage();
            stageAñadir.initModality(Modality.APPLICATION_MODAL);
            stageAñadir.setResizable(false);
            stageAñadir.setScene(scAñadirJuego);
            stageAñadir.setTitle("Añadir Juego");
            stageAñadir.show();
            cerrarVentana();
        }
    }

    private void ocultarTodasLasTablas() {
        for (Node node : contenedorTablas.getChildren()) {

            //node.toBack();
            node.setVisible(false);
        }

    }

    public ObservableList<Cliente> obtenerClientesBD() {
        System.out.println("CLIENTES");
        clientes = FXCollections.observableArrayList();
        if (conexion != null) {
            String sql = """
                    SELECT dni, nombre, telefono, email, socio
                    FROM Clientes
                    """;
            try {
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    Cliente cliente = new Cliente(
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getInt("telefono"),
                            rs.getString("email"),
                            rs.getBoolean("socio")
                    );
                    clientes.add(cliente);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return clientes;
        }
        return null;
    }

    public ObservableList<Compras> obtenerComprasBD() {
        ObservableList<Compras> compras = FXCollections.observableArrayList();
        if (conexion != null) {
            String sql = """
                    SELECT Fecha_Compra, dni, id_Juego
                    FROM Compras
                    """;
            try {
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    Compras compra = new Compras(
                            rs.getDate("Fecha_Compra"),
                            rs.getString("dni"),
                            rs.getInt("id_Juego")
                    );
                    compras.add(compra);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: " + e.getMessage());
            }
            return compras;
        }
        return null;
    }

    public  ObservableList<Juego> obtenerJuegosBD() {
        ObservableList<Juego> juegos = FXCollections.observableArrayList();
        if (conexion != null) {
            String sql = """
                    SELECT id_Juego, Nombre, Descripcion, Plataforma, Imagen, Stock, Precio
                    FROM Juegos
                    """;
            try {
                rs = st.executeQuery(sql);
                while (rs.next()) {
                    Juego juego = new Juego(
                            rs.getInt("id_Juego"),
                            rs.getString("Imagen"),
                            rs.getString("Nombre"),
                            rs.getString("Plataforma"),
                            rs.getString("Descripcion"),
                            rs.getInt("Stock"),
                            rs.getDouble("Precio")
                    );
                    juegos.add(juego);
                }
            } catch (SQLException e) {
                System.out.println("Excepción SQL: " + e.getMessage());
            }
            return juegos;
        }
        return null;
    }

    private void inicializarImagenes() {
        try {
            imgAñadir.setImage(new Image(getClass().getResourceAsStream("/imagenes/añadir.png")));
            imgClientes.setImage(new Image(getClass().getResourceAsStream("/imagenes/clientes.png")));
            imgCompra.setImage(new Image(getClass().getResourceAsStream("/imagenes/compra.png")));
            imgJuegos.setImage(new Image(getClass().getResourceAsStream("/imagenes/juegos.png")));
        } catch (Exception e) {
            System.out.println("Error al cargar las imágenes: " + e.getMessage());
        }
    }

    @FXML
    void salir(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("¿Estás seguro de que deseas cerrar la ventana?");
        alert.setContentText("Los cambios realizados se guardarán.");
        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            System.exit(0);
        }
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

    public Connection getConnection() throws IOException {

        Properties properties = new Properties();
        String IP, PORT, BBDD, USER, PWD;
        try {
            InputStream input_ip = new FileInputStream("ip.properties");
            properties.load(input_ip);
            IP = (String) properties.get("IP");
        } catch (FileNotFoundException e) {
            System.out.println("No se pudo encontrar el archivo de propiedades para IP, se establece localhost por defecto");
            IP = "localhost";
        }

        InputStream input = Controlador.class.getClassLoader().getResourceAsStream("bbdd.properties");
        if (input == null) {
            System.out.println("No se pudo encontrar el archivo de propiedades");
            return null;
        } else {

            properties.load(input);

            PORT = (String) properties.get("PORT");
            BBDD = (String) properties.get("BBDD");
            USER = (String) properties.get("USER");//USER de MARIADB en LAMP 
            PWD = (String) properties.get("PWD");//PWD de MARIADB en LAMP 

            Connection conn;
            try {
                String cadconex = "jdbc:mysql://" + IP + ":" + PORT + "/" + BBDD + " USER:" + USER + "PWD:" + PWD;
                System.out.println(cadconex);
                //Si usamos LAMP Funciona con ambos conectores
                conn = DriverManager.getConnection("jdbc:mysql://" + IP + ":" + PORT + "/" + BBDD, USER, PWD);
                return conn;
            } catch (SQLException e) {
                System.out.println("Error SQL: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Ha ocurrido un error de conexión");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
                exit(0);
                return null;
            }
        }
    }

    void introducirClientes() {
        dni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        socio.setCellValueFactory(new PropertyValueFactory<>("socio"));
        tablaClientes.setItems(obtenerClientesBD());
    }

    void introducirCompras() {
        fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        cliente.setCellValueFactory(new PropertyValueFactory<>("dni"));
        idJuego.setCellValueFactory(new PropertyValueFactory<>("idjuego"));
        tablaCompras.setItems(obtenerComprasBD());
    }

    void introducirJuegos() {
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conexion = this.getConnection();
            if (conexion != null) {
                this.st = conexion.createStatement();
            }
        } catch (IOException | SQLException e) {
        }
        if (conexion != null) {
            introducirClientes();
            introducirCompras();
            introducirJuegos();
            ocultar();
            inicializarImagenes();
        }
    }
}
