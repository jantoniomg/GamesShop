package controladores;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Juanan
 */
public class ConexionMySQL {
        private static final String url = "jdbc:mysql://localhost:3306/GamesShop"; 
        private static final String usuario = "admin"; 
        private static final String contraseña = "nsR7h5fwElFj"; 
    public static Connection conectarBaseDeDatos() {
        Connection conexion=null;
        try {
            conexion = DriverManager.getConnection(url, usuario, contraseña);
            System.out.println("Conexión exitosa a la base de datos MySQL");
        } catch (SQLException e) {
            e.printStackTrace();
        }    
        return conexion;
    }
}
