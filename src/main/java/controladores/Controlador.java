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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Juanan
 */
public class Controlador implements Initializable {

    private FXMLLoader loader;
    private controladorAñadirCliente conAñadirCliente;
    private controladorAñadirJuego conAñadirJuego;
    private controladorAñadirCompra conAñadirCompra;
    private ObservableList<Cliente> clientes;
    private ObservableList<Compras> compras;
    private ObservableList<Juego> juegos;
    
    boolean editrarBool;
    Juego juegoGuardado;
    Cliente clienteGuardado;
    Compras compraGuardada;

    Stage stageAñadir;
    Connection conexion;
    Statement st;
    ResultSet rs;

    Integer tabla = 1;

    ScaleTransition scaleTransitionA;
    ScaleTransition scaleTransitionB;

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
    
    private Image iconoApp(){
        Image icon = new Image(getClass().getResourceAsStream("/imagenes/logo.png"));
        return icon;
    }
    
    //Se utiliza para inicializar las imagenes que se usan en la aplicacion al iniciarse
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

    //Se utilizan para poder usar la funcion de editar
    public Boolean editando() {
        return editrarBool;
    }

    public Compras dameCompra() {
        return compraGuardada;
    }

    public Cliente dameCliente() {
        return clienteGuardado;
    }

    public Juego dameJuego() {
        return juegoGuardado;
    }

    //se usa para ocultar todas las tablas y solo mostrar la de compras esto se usa al iniciar la app
    private void ocultar() {
        for (Node node : contenedorTablas.getChildren()) {
            node.setVisible(false);
        }
        paneCompras.setVisible(true);
        tablaCompras.setVisible(true);
    }

    //Se usa para cuando se va a cerrar una ventana
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

    //Abrir ventanas que se usan tanto para añadir como para editar
    public void abrirAñadirCompras(String titulo) throws Exception {
        loader = new FXMLLoader(getClass().getResource("../ventanas/añadirCompras.fxml"));
        Parent root = loader.load();
        conAñadirCompra = loader.getController();
        conAñadirCompra.setControladorEnlace(this);
        Scene scAñadirCompra = new Scene(root);
        stageAñadir = new Stage();
        stageAñadir.getIcons().add(iconoApp());
        stageAñadir.initModality(Modality.APPLICATION_MODAL);
        stageAñadir.setResizable(false);
        stageAñadir.setScene(scAñadirCompra);
        stageAñadir.setTitle(titulo);
        stageAñadir.show();
        cerrarVentana();
    }

    public void abrirAñadirCliente(String titulo) throws Exception {
        loader = new FXMLLoader(getClass().getResource("../ventanas/añadirCliente.fxml"));
        Parent root = loader.load();
        conAñadirCliente = loader.getController();
        conAñadirCliente.setControladorEnlace(this);
        Scene scAñadirCliente = new Scene(root);
        stageAñadir = new Stage();
        stageAñadir.getIcons().add(iconoApp());
        stageAñadir.initModality(Modality.APPLICATION_MODAL);
        stageAñadir.setResizable(false);
        stageAñadir.setScene(scAñadirCliente);
        stageAñadir.setTitle(titulo);
        stageAñadir.show();
        cerrarVentana();
    }

    public void abrirAñadirJuego(String titulo) throws Exception {
        loader = new FXMLLoader(getClass().getResource("../ventanas/añadirJuego.fxml"));
        Parent root = loader.load();
        conAñadirJuego = loader.getController();
        conAñadirJuego.setControladorEnlace(this);
        Scene scAñadirJuego = new Scene(root);
        stageAñadir = new Stage();
        stageAñadir.getIcons().add(iconoApp());
        stageAñadir.initModality(Modality.APPLICATION_MODAL);
        stageAñadir.setResizable(false);
        stageAñadir.setScene(scAñadirJuego);
        stageAñadir.setTitle(titulo);
        stageAñadir.show();
        cerrarVentana();
    }

    //se usa para ocultar las tablas
    private void ocultarTodasLasTablas() {
        for (Node node : contenedorTablas.getChildren()) {
            node.setVisible(false);
        }
    }

    //Ya que el context menu de la tabla compras falla tenemos estos botones auxiliares que solo se muestran en compras
    @FXML
    void actualizarCompra(ActionEvent event) throws Exception {
        Compras compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra != null) {
            editrarBool = true;
            compraGuardada = compra;
            abrirAñadirCompras("Editar Compra");
        } else {
            System.out.println("Selecciona una Compra para actualizarla");
        }
    }

    @FXML
    void eliminarCompra(ActionEvent event) {
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

    //se usa para ocultar o mostrar los dos botones anteriores
    void botonesVisibles() {
        eliminar.setVisible(true);
        actualizar.setVisible(true);
    }

    void botonesInvisibles() {
        eliminar.setVisible(false);
        actualizar.setVisible(false);
    }

    //botones que abren las "ventanas" de juegos, compras y clientes.
    //Cuando se le da al boton muestra la tabla y las opciones correspondientes  
    @FXML
    void abrirVentanaCompras(ActionEvent event) {
        tabla = 1;
        ocultarTodasLasTablas();
        paneCompras.setVisible(true);
        tablaCompras.setVisible(true);
        botonesVisibles();
        //animacion
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1.5), tablaCompras);
        slideIn.setFromX(-570);
        slideIn.setToX(0);
        slideIn.play();
        ContextMenu cmCompras = new ContextMenu();
        MenuItem editar = new MenuItem("editar");
        MenuItem eliminar = new MenuItem("eliminar");
        cmCompras.getItems().addAll(editar, eliminar);
        tablaCompras.setContextMenu(cmCompras);
        tablaCompras.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                editar.setOnAction(a -> {
                    try {
                        editrarBool = true;
                        compraGuardada = nuevoValor;
                        abrirAñadirCompras("Editar Compra");
                    } catch (Exception e) {
                        System.out.println("Excepción: " + e.getMessage());
                    }
                });
                eliminar.setOnAction(v -> {
                    borrarCompra(nuevoValor);
                });
                tablaCompras.setOnKeyPressed(evento -> {
                    if (evento.getCode() == KeyCode.DELETE) {
                            borrarCompra(nuevoValor);
                        }
                    
                });
            }
        });
    }

    private void borrarCompra(Compras compra) {
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
        }
    }

    @FXML
    void abrirVentanaClientes(ActionEvent event) {
        botonesInvisibles();
        tabla = 2;
        ocultarTodasLasTablas();
        paneClientes.setVisible(true);
        tablaClientes.setVisible(true);
        //animacion
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1.5), tablaClientes);
        slideIn.setFromX(-570);
        slideIn.setToX(0);
        slideIn.play();
        ContextMenu cmClientes = new ContextMenu();
        MenuItem editar = new MenuItem("editar");
        MenuItem eliminar = new MenuItem("eliminar");
        cmClientes.getItems().addAll(editar, eliminar);
        tablaClientes.setContextMenu(cmClientes);
        tablaClientes.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                editar.setOnAction(a -> {
                    try {
                        editrarBool = true;
                        clienteGuardado = nuevoValor;
                        abrirAñadirCliente("Editar Cliente");
                    } catch (Exception e) {
                        System.out.println("Excepción: " + e.getMessage());
                    }
                });
                eliminar.setOnAction(v -> {
                    eliminarCliente(nuevoValor);
                });
                tablaClientes.setOnKeyPressed(evento -> {
                    if (evento.getCode() == KeyCode.DELETE) {
                        eliminarCliente(nuevoValor);

                    }
                });
            }
        });
    }
    private void eliminarCliente(Cliente cliente) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar el cliente?");
        alert.setContentText("El cliente se eliminara para siempre");
        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            String sql = "DELETE FROM Clientes WHERE dni=?";
            try {
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setString(1, cliente.getDni());
                ps.executeUpdate();
                introducirClientes();
                System.out.println("Se ha eliminado el Cliente con Dni " + cliente.getDni());
            } catch (SQLException exception) {
                System.out.println("Excepción: " + exception.getMessage());
            }
        }
    }

    @FXML
    void abrirVentanaJuegos(ActionEvent event) throws Exception {
        tabla = 3;
        ocultarTodasLasTablas();
        paneJuegos.setVisible(true);
        tablaJuegos.setVisible(true);
        TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1.5), tablaJuegos);
        slideIn.setFromX(-570);
        slideIn.setToX(0);
        slideIn.play();
        ContextMenu cmJuegos = new ContextMenu();
        MenuItem editar = new MenuItem("editar");
        MenuItem eliminar = new MenuItem("eliminar");
        cmJuegos.getItems().addAll(editar, eliminar);
        tablaJuegos.setContextMenu(cmJuegos);
        tablaJuegos.getSelectionModel().selectedItemProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                editar.setOnAction(a -> {
                    try {
                        editrarBool = true;
                        juegoGuardado = nuevoValor;
                        abrirAñadirJuego("Editar juego");
                    } catch (Exception e) {
                        System.out.println("Excepción: " + e.getMessage());
                    }
                });
                eliminar.setOnAction(v -> {
                    eliminarJuego(nuevoValor);
                });
                tablaJuegos.setOnKeyPressed(evento -> {
                    if (evento.getCode() == KeyCode.DELETE) {
                        eliminarJuego(nuevoValor);
                    }
                });
            }
        });
    }
    //metodo para eliminar 

    private void eliminarJuego(Juego juego) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Advertencia");
        alert.setHeaderText("¿Estás seguro de que deseas eliminar el juego?");
        alert.setContentText("El juego se eliminara para siempre");
        Optional<ButtonType> respuesta = alert.showAndWait();
        if (respuesta.isPresent() && respuesta.get() == ButtonType.OK) {
            String sql = "DELETE FROM Juegos WHERE id_Juego=?";
            try {
                PreparedStatement ps = conexion.prepareStatement(sql);
                ps.setInt(1, juego.getId_juego());
                ps.executeUpdate();
                introducirJuegos();
                System.out.println("Se ha eliminado el juego con ID " + juego.getId_juego());
            } catch (SQLException exception) {
                System.out.println("No se ha podido eliminar poque tiene compras");
                System.out.println("Excepción: " + exception.getMessage());
            }
        }
    }

    //Se usa para abrir la ventana de añadir segun la tabla que se esta mostrando
    @FXML
    void añadirElemento(ActionEvent e) throws Exception {
        if (tabla == 1) {
            editrarBool = false;
            abrirAñadirCompras("Añadir Compra");

        } else if (tabla == 2) {
            editrarBool = false;
            abrirAñadirCliente("Añadir Cliente");

        } else {
            editrarBool = false;
            abrirAñadirJuego("Añadir Juego");
        }
    }

    //Se usan para sacar los datos de cada tabla de la base de datos 
    //y rellenar una lista de esos objetos para utilizarlos mas a delante
    public ObservableList<Compras> obtenerComprasBD() {
        compras = FXCollections.observableArrayList();
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

    public ObservableList<Cliente> obtenerClientesBD() {
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
                            rs.getString("telefono"),
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

    public ObservableList<Juego> obtenerJuegosBD() {
        juegos = FXCollections.observableArrayList();
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

    //boton para salir de la app
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

    //carga la imagen en la tabla
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
            USER = (String) properties.get("USER");
            PWD = (String) properties.get("PWD"); 

            Connection conn;
            try {
                String cadconex = "jdbc:mysql://" + IP + ":" + PORT + "/" + BBDD + " USER:" + USER + "PWD:" + PWD;
                System.out.println(cadconex);
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

    //animaciones
    private void animaciones() {
        List<Node> lista = List.of(btnAñadir, btnClientes, btnCompras, btnJuegos, btnSalir);
        for (Node n : lista) {
            n.setOnMouseEntered(event -> {
                scaleTransitionA = new ScaleTransition(Duration.seconds(0.2), n);
                scaleTransitionA.setToX(1.2);
                scaleTransitionA.setToY(1.2);
                scaleTransitionA.play();
            });
            n.setOnMouseExited(event -> {
                this.scaleTransitionA.stop();
                scaleTransitionB = new ScaleTransition(Duration.seconds(0.1), n);
                scaleTransitionB.setToX(1);
                scaleTransitionB.setToY(1);
                scaleTransitionB.play();
            });
        }

        List<TableView> tablas = Arrays.asList(tablaCompras, tablaClientes, tablaJuegos);
        ParallelTransition parallelTransition = new ParallelTransition();
        for (TableView<?> tabla : tablas) {
            TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1.5), tabla);
            slideIn.setToX(0);
            parallelTransition.getChildren().add(slideIn);
        }
        parallelTransition.play();
        /*TranslateTransition slideIn = new TranslateTransition(Duration.seconds(1.5), tablaCompras);
        slideIn.setToX(0);
        slideIn.play();*/
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
            animaciones();
            introducirClientes();
            introducirCompras();
            introducirJuegos();
            ocultar();
            inicializarImagenes();
            btnAñadir.setOnKeyPressed(event -> {
                if (event.isAltDown() && event.getCode().toString().equals("a")) {
                    try {
                        añadirElemento(new ActionEvent(btnAñadir, null));
                    } catch (Exception e) {
                        System.out.println("Error al abrir el botón Añadir: " + e.getMessage());
                    }
                }
            });
        }
    }
}
